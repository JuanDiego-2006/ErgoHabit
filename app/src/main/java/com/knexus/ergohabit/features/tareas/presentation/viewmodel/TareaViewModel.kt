package com.knexus.ergohabit.features.tareas.presentation.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knexus.ergohabit.features.tareas.domain.entities.TareaEnfoque
import com.knexus.ergohabit.features.tareas.domain.usecases.*
import com.knexus.ergohabit.features.tareas.presentation.receiver.AlertaSaludReceiver
import com.knexus.ergohabit.features.tareas.presentation.screens.TareaUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TareaViewModel @Inject constructor(
    private val getTareasUseCase: GetTareasUseCase,
    private val createTareaUseCase: CreateTareaUseCase,
    private val completarTareaUseCase: CompletarTareaUseCase,
    private val iniciarTareaUseCase: IniciarTareaUseCase,
    private val pausarTareaUseCase: PausarTareaUseCase,
    private val extenderTareaUseCase: ExtenderTareaUseCase,
    private val eliminarTareaUseCase: EliminarTareaUseCase,
    private val getAlertaSaludUseCase: GetAlertaSaludUseCase,
    private val saveLocalProgressUseCase: SaveLocalProgressUseCase,
    private val getLocalProgressUseCase: GetLocalProgressUseCase,
    private val clearLocalProgressUseCase: ClearLocalProgressUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(TareaUiState())
    val uiState: StateFlow<TareaUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var segundosTranscurridos = 0
    private var segundosParaAlertaHealth = 0
    private val INTERVALO_ALERTA_API = 25 * 60
    private val INTERVALO_ESTIRAMIENTO = 25 * 60

    init {
        loadTareas()
    }

    fun prepararPantallaCompletado(idTarea: Int) {
        viewModelScope.launch {
            val tarea = (_uiState.value.tareasEstado?.pendientes ?: emptyList()).find { it.id == idTarea }
            if (tarea != null) {
                _uiState.update { it.copy(tareaSeleccionada = tarea, mostrarSheetCompletado = true) }
            } else {
                getTareasUseCase().collect { result ->
                    result.onSuccess { estado ->
                        _uiState.update { it.copy(tareasEstado = estado) }
                        val t = estado.pendientes.find { it.id == idTarea }
                        if (t != null) {
                            _uiState.update { it.copy(tareaSeleccionada = t, mostrarSheetCompletado = true) }
                        }
                    }
                }
            }
        }
    }

    fun loadTareas() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getTareasUseCase().collect { result ->
                result.onSuccess { estado ->
                    _uiState.update { it.copy(tareasEstado = estado, isLoading = false) }
                    
                    val actual = _uiState.value.tareaSeleccionada
                    if (actual != null) {
                        (estado.pendientes + estado.completadas).find { it.id == actual.id }?.let { actualizada ->
                            _uiState.update { it.copy(tareaSeleccionada = actualizada) }
                            
                            // Sincronización al recargar
                            if (actualizada.idEstado == 3 && !_uiState.value.isTimerRunning) {
                                recuperarYIniciarSincronizado(actualizada)
                            }
                        }
                    } else {
                        estado.pendientes.find { it.idEstado == 3 }?.let { tareaActiva ->
                            recuperarYIniciarSincronizado(tareaActiva)
                        }
                    }
                }.onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
            }
        }
    }

    private fun recuperarYIniciarSincronizado(tarea: TareaEnfoque) {
        viewModelScope.launch {
            val progreso = getLocalProgressUseCase(tarea.id)
            val ahora = System.currentTimeMillis()
            
            val segundosAPI = tarea.duracionMinutos * 60
            
            // Lógica Maestra: Si hay un targetEndTimeMs guardado y es futuro, calculamos el tiempo real pasado
            val segundos = if (progreso != null && progreso.targetEndTimeMs > ahora) {
                ((progreso.targetEndTimeMs - ahora) / 1000).toInt()
            } else if (progreso != null && Math.abs(progreso.segundosRestantes - segundosAPI) < 65) {
                progreso.segundosRestantes
            } else {
                segundosAPI
            }
            
            val inicial = progreso?.segundosIniciales ?: (tarea.duracionMinutos * 60)

            _uiState.update { 
                it.copy(
                    tareaSeleccionada = tarea,
                    tiempoRestante = segundos,
                    duracionSesionActual = inicial,
                    targetEndTimeMs = progreso?.targetEndTimeMs ?: -1L,
                    isTimerRunning = (tarea.idEstado == 3 && segundos > 0)
                )
            }
            if (_uiState.value.isTimerRunning) startTimerVisual()
        }
    }

    fun seleccionarTarea(tarea: TareaEnfoque) {
        if (_uiState.value.tareaSeleccionada?.id == tarea.id) return
        viewModelScope.launch {
            // Guardar progreso de la tarea que dejamos
            _uiState.value.tareaSeleccionada?.let { anterior ->
                saveLocalProgressUseCase(
                    anterior.id, 
                    _uiState.value.tiempoRestante, 
                    _uiState.value.duracionSesionActual, 
                    if (_uiState.value.isTimerRunning) _uiState.value.targetEndTimeMs else -1L
                )
            }

            timerJob?.cancel()
            segundosTranscurridos = 0
            
            val progreso = getLocalProgressUseCase(tarea.id)
            val ahora = System.currentTimeMillis()
            val segundosAPI = tarea.duracionMinutos * 60

            val segundos = if (tarea.idEstado == 3 && progreso != null && progreso.targetEndTimeMs > ahora) {
                ((progreso.targetEndTimeMs - ahora) / 1000).toInt()
            } else if (progreso != null && Math.abs(progreso.segundosRestantes - segundosAPI) < 65) {
                progreso.segundosRestantes
            } else {
                segundosAPI
            }
            
            val inicial = progreso?.segundosIniciales ?: (tarea.duracionMinutos * 60)

            _uiState.update { 
                it.copy(
                    tareaSeleccionada = tarea, 
                    tiempoRestante = segundos, 
                    duracionSesionActual = inicial, 
                    targetEndTimeMs = progreso?.targetEndTimeMs ?: -1L,
                    isTimerRunning = (tarea.idEstado == 3 && segundos > 0)
                ) 
            }
            if (_uiState.value.isTimerRunning) startTimerVisual()
        }
    }

    fun toggleTimer() {
        val tarea = _uiState.value.tareaSeleccionada ?: return
        if (_uiState.value.isTimerRunning) {
            viewModelScope.launch {
                pausarTareaUseCase(tarea.id).onSuccess { mensaje ->
                    timerJob?.cancel()
                    _uiState.update { it.copy(isTimerRunning = false, targetEndTimeMs = -1L) }
                    cancelarTodasLasAlarmas(tarea.id)
                    saveLocalProgressUseCase(tarea.id, _uiState.value.tiempoRestante, _uiState.value.duracionSesionActual, -1L)
                    _uiState.update { it.copy(successMessage = mensaje) }
                    loadTareas()
                }.onFailure { error -> _uiState.update { it.copy(error = error.message) } }
            }
        } else {
            viewModelScope.launch {
                iniciarTareaUseCase(tarea.id).onSuccess { mensaje ->
                    val duration = _uiState.value.tiempoRestante
                    val targetEnd = System.currentTimeMillis() + (duration.toLong() * 1000)
                    
                    val inicial = if (_uiState.value.duracionSesionActual > 0) _uiState.value.duracionSesionActual else duration
                    
                    _uiState.update { it.copy(successMessage = mensaje, duracionSesionActual = inicial, targetEndTimeMs = targetEnd) }
                    saveLocalProgressUseCase(tarea.id, duration, inicial, targetEnd)
                    
                    programarAlarmaSistema(tarea.id, (25 * 60).toLong(), "ALERTA_SALUD")
                    programarAlarmaSistema(tarea.id, duration.toLong(), "FIN_TAREA")
                    
                    startTimerVisual()
                    loadTareas()
                }.onFailure { error -> _uiState.update { it.copy(error = error.message) } }
            }
        }
    }

    private fun startTimerVisual() {
        if (_uiState.value.tiempoRestante <= 0) return
        timerJob?.cancel()
        _uiState.update { it.copy(isTimerRunning = true) }
        timerJob = viewModelScope.launch {
            while (_uiState.value.tiempoRestante > 0) {
                delay(1000)
                segundosTranscurridos++
                segundosParaAlertaHealth++
                
                if (segundosTranscurridos >= INTERVALO_ESTIRAMIENTO) {
                    _uiState.update { it.copy(mostrarRecordatorioEstiramiento = true) }
                    segundosTranscurridos = 0
                }
                
                if (segundosParaAlertaHealth >= INTERVALO_ALERTA_API) {
                    _uiState.value.tareaSeleccionada?.let { t -> 
                        getAlertaSaludUseCase(t.id).onSuccess { info -> 
                            _uiState.update { it.copy(mostrarAlertaSalud = true, alertaSaludInfo = info) } 
                        } 
                    }
                    segundosParaAlertaHealth = 0
                }

                val ahora = System.currentTimeMillis()
                val targetEnd = _uiState.value.targetEndTimeMs
                
                if (targetEnd > 0) {
                    val nuevoRestante = ((targetEnd - ahora) / 1000).toInt()
                    _uiState.update { it.copy(tiempoRestante = if (nuevoRestante > 0) nuevoRestante else 0) }
                } else {
                    _uiState.update { it.copy(tiempoRestante = it.tiempoRestante - 1) }
                }
                
                if (_uiState.value.tiempoRestante % 5 == 0) {
                    _uiState.value.tareaSeleccionada?.let { t ->
                        saveLocalProgressUseCase(t.id, _uiState.value.tiempoRestante, _uiState.value.duracionSesionActual, _uiState.value.targetEndTimeMs)
                    }
                }
            }
            
            _uiState.value.tareaSeleccionada?.let { t -> 
                pausarTareaUseCase(t.id)
                cancelarTodasLasAlarmas(t.id)
                clearLocalProgressUseCase(t.id)
            }
            _uiState.update { it.copy(isTimerRunning = false, mostrarSheetCompletado = true, tiempoRestante = 0) }
        }
    }

    fun eliminarTarea(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            eliminarTareaUseCase(id).onSuccess { mensaje ->
                cancelarTodasLasAlarmas(id)
                clearLocalProgressUseCase(id)
                _uiState.update { it.copy(successMessage = mensaje, isLoading = false) }
                if (_uiState.value.tareaSeleccionada?.id == id) {
                    timerJob?.cancel()
                    _uiState.update { it.copy(tareaSeleccionada = null, tiempoRestante = 0, isTimerRunning = false, duracionSesionActual = 0) }
                }
                loadTareas()
            }.onFailure { error -> _uiState.update { it.copy(error = error.message, isLoading = false) } }
        }
    }

    fun completarTarea() {
        val t = _uiState.value.tareaSeleccionada ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, mostrarSheetCompletado = false) }
            
            val flowIniciar = if (t.idEstado != 3) iniciarTareaUseCase(t.id) else Result.success("Ya activa")
            
            flowIniciar.onSuccess {
                completarTareaUseCase(t.id).onSuccess { mensaje ->
                    cancelarTodasLasAlarmas(t.id)
                    clearLocalProgressUseCase(t.id)
                    _uiState.update { it.copy(isLoading = false, mostrarMensajeExito = true, mensajeExito = mensaje, tareaSeleccionada = null, duracionSesionActual = 0, tiempoRestante = 0) }
                    loadTareas()
                }.onFailure { error -> _uiState.update { it.copy(error = error.message, isLoading = false) } }
            }.onFailure { error ->
                _uiState.update { it.copy(error = "No se pudo reactivar para completar: ${error.message}", isLoading = false) }
            }
        }
    }

    fun extenderTarea(minutos: Int) {
        val t = _uiState.value.tareaSeleccionada ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, mostrarSheetMasTiempo = false) }
            
            val flowIniciar = if (t.idEstado != 3) iniciarTareaUseCase(t.id) else Result.success("Ya activa")
            
            flowIniciar.onSuccess {
                extenderTareaUseCase(t.id, minutos).onSuccess { mensaje ->
                    val segundosExtras = minutos * 60
                    _uiState.update { it.copy(
                        successMessage = mensaje,
                        isLoading = false,
                        tiempoRestante = segundosExtras,
                        duracionSesionActual = segundosExtras,
                        isTimerRunning = false 
                    ) }
                    saveLocalProgressUseCase(t.id, segundosExtras, segundosExtras, -1L)
                    loadTareas()
                }.onFailure { error -> _uiState.update { it.copy(error = error.message, isLoading = false) } }
            }.onFailure { error ->
                _uiState.update { it.copy(error = "No se pudo reactivar para extender: ${error.message}", isLoading = false) }
            }
        }
    }

    private fun programarAlarmaSistema(idTarea: Int, segundos: Long, tipo: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlertaSaludReceiver::class.java).apply { putExtra("idTarea", idTarea); if (tipo == "FIN_TAREA") putExtra("esFinDeTarea", true) }
        val requestId = if (tipo == "FIN_TAREA") idTarea + 1000 else idTarea
        val pendingIntent = PendingIntent.getBroadcast(context, requestId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val triggerTime = System.currentTimeMillis() + (segundos * 1000)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        } else {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    }

    private fun cancelarTodasLasAlarmas(idTarea: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlertaSaludReceiver::class.java)
        listOf(idTarea, idTarea + 1000).forEach { id ->
            val pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
            if (pendingIntent != null) alarmManager.cancel(pendingIntent)
        }
    }

    fun mostrarAlertaDesdeNotificacion(frase: String, accion: String) { _uiState.update { it.copy(mostrarAlertaSalud = true, alertaSaludInfo = com.knexus.ergohabit.features.tareas.domain.entities.AlertaSalud(frase, accion, true)) } }
    fun clearMessages() { _uiState.update { it.copy(error = null, successMessage = null) } }
    fun descartarRecordatorio() { _uiState.update { it.copy(mostrarRecordatorioEstiramiento = false) }; segundosTranscurridos = 0 }
    fun descartarAlertaSalud() { _uiState.update { it.copy(mostrarAlertaSalud = false) }; segundosParaAlertaHealth = 0 }
    fun mostrarSheetNuevaTarea(m: Boolean) { _uiState.update { it.copy(mostrarSheetNuevaTarea = m) } }
    fun onTituloCambiado(t: String) { _uiState.update { it.copy(nuevoTitulo = t) } }
    fun onCategoriaSeleccionada(n: String) { _uiState.update { it.copy(nuevaCategoriaNombre = n) } }
    fun onDuracionCambiada(m: Int) { _uiState.update { it.copy(nuevaDuracion = m, mostrarSelectorDuracion = false) } }
    fun onNumeroPresionado(n: String) { _uiState.update { s -> s.copy(nuevaDuracionInput = (s.nuevaDuracionInput + n).takeLast(4)) } }
    fun onBorrarPresionado() { _uiState.update { s -> s.copy(nuevaDuracionInput = ("0" + s.nuevaDuracionInput.dropLast(1)).takeLast(4)) } }
    fun confirmarDuracion() {
        val s = _uiState.value
        val total = ((s.nuevaDuracionInput.substring(0, 2).toIntOrNull() ?: 0) * 60) + (s.nuevaDuracionInput.substring(2, 4).toIntOrNull() ?: 0)
        _uiState.update { it.copy(nuevaDuracion = if (total > 0) total else 1, mostrarSelectorDuracion = false, nuevaDuracionInput = "0000") }
    }
    fun mostrarSelectorDuracion(m: Boolean) { _uiState.update { it.copy(mostrarSelectorDuracion = m, nuevaDuracionInput = "0000") } }
    fun mostrarSheetCompletado(m: Boolean) { _uiState.update { it.copy(mostrarSheetCompletado = m) } }
    fun mostrarSheetMasTiempo(m: Boolean) { _uiState.update { it.copy(mostrarSheetMasTiempo = m, mostrarSheetCompletado = !m) } }
    fun actualizarTiempoAdicional(m: Int) { _uiState.update { it.copy(tiempoAdicional = m.coerceIn(15, 120)) } }
    fun descartarMensajeExito() { _uiState.update { it.copy(mostrarMensajeExito = false) } }
    fun agregarMasTiempo(m: Int) { extenderTarea(m) }

    fun agregarTarea() {
        val s = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, mostrarSheetNuevaTarea = false) }
            createTareaUseCase(s.nuevoTitulo, s.nuevaCategoriaNombre, s.nuevaDuracion).onSuccess { msg -> _uiState.update { it.copy(successMessage = msg, nuevoTitulo = "", nuevaDuracion = 0) }; loadTareas() }.onFailure { error -> _uiState.update { it.copy(error = error.message, isLoading = false) } }
        }
    }
}
