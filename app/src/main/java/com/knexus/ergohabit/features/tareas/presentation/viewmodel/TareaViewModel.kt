package com.knexus.ergohabit.features.tareas.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knexus.ergohabit.core.session.SessionManager
import com.knexus.ergohabit.features.tareas.domain.entities.TareaEnfoque
import com.knexus.ergohabit.features.tareas.domain.usecases.*
import com.knexus.ergohabit.features.tareas.presentation.screens.TareaUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.knexus.ergohabit.features.tareas.presentation.receiver.AlertaSaludReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class TareaViewModel @Inject constructor(
    private val getTareasUseCase: GetTareasUseCase,
    private val createTareaUseCase: CreateTareaUseCase,
    private val completarTareaUseCase: CompletarTareaUseCase,
    private val iniciarTareaUseCase: IniciarTareaUseCase,
    private val pausarTareaUseCase: PausarTareaUseCase,
    private val eliminarTareaUseCase: EliminarTareaUseCase,
    private val getAlertaSaludUseCase: GetAlertaSaludUseCase,
    private val sessionManager: SessionManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(TareaUiState())
    val uiState: StateFlow<TareaUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var segundosTranscurridos = 0
    private var segundosParaAlerta = 0 // Nuevo contador para la alerta de 30 min
    private val INTERVALO_ALERTA_API = 30 * 60 // 30 minutos
    private val INTERVALO_ESTIRAMIENTO = 25 * 60

    init {
        loadTareas()
    }

    fun loadTareas() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getTareasUseCase().collect { result ->
                result.onSuccess { estado ->
                    _uiState.update { it.copy(tareasEstado = estado, isLoading = false) }
                    
                    // Al cargar, si ya hay una seleccionada, actualizamos su estado desde el servidor
                    val actual = _uiState.value.tareaSeleccionada
                    if (actual != null) {
                        val actualizada = (estado.pendientes + estado.completadas).find { it.id == actual.id }
                        if (actualizada != null) {
                            _uiState.update { it.copy(tareaSeleccionada = actualizada) }
                        }
                    } else {
                        // Si no hay ninguna, buscamos si hay una activa (ID 3) para mostrarla pausada con su tiempo guardado
                        estado.pendientes.find { it.idEstado == 3 }?.let { tareaActiva ->
                            recuperarTareaPausada(tareaActiva)
                        }
                    }
                }.onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
            }
        }
    }

    /**
     * Muestra el diálogo de alerta de salud cuando se abre la app desde una notificación.
     */
    fun mostrarAlertaDesdeNotificacion(frase: String, accion: String) {
        _uiState.update { 
            it.copy(
                mostrarAlertaSalud = true,
                alertaSaludInfo = com.knexus.ergohabit.features.tareas.domain.entities.AlertaSalud(
                    frase = frase,
                    accion = accion,
                    requierePostura = true
                )
            )
        }
    }

    private fun recuperarTareaPausada(tarea: TareaEnfoque) {
        val guardado = sessionManager.getTaskProgress(tarea.id)
        val tiempoInicial = if (guardado > 0) guardado else calcularSegundosDesdeServidor(tarea)
        
        _uiState.update { 
            it.copy(
                tareaSeleccionada = tarea,
                tiempoRestante = tiempoInicial,
                isTimerRunning = false // Siempre pausado al recuperar para evitar auto-inicio
            )
        }
    }

    private fun calcularSegundosDesdeServidor(tarea: TareaEnfoque): Int {
        val fechaInicioStr = tarea.fechaInicioCronometro ?: return tarea.duracionMinutos * 60
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val inicio = sdf.parse(fechaInicioStr) ?: return tarea.duracionMinutos * 60
            val transcurrido = (System.currentTimeMillis() - inicio.time) / 1000
            val restante = (tarea.duracionMinutos * 60 - transcurrido).toInt()
            if (restante > 0) restante else 0
        } catch (e: Exception) {
            tarea.duracionMinutos * 60
        }
    }

    fun seleccionarTarea(tarea: TareaEnfoque) {
        // Guardar progreso de la actual antes de cambiar
        _uiState.value.tareaSeleccionada?.let { anterior ->
            sessionManager.saveTaskProgress(anterior.id, _uiState.value.tiempoRestante)
        }
        
        stopTimer()
        segundosTranscurridos = 0
        
        // Recuperar progreso guardado o calcular nuevo
        val guardado = sessionManager.getTaskProgress(tarea.id)
        val tiempoInicial = when {
            guardado > 0 -> guardado
            tarea.idEstado == 3 -> calcularSegundosDesdeServidor(tarea)
            else -> tarea.duracionMinutos * 60
        }

        _uiState.update { 
            it.copy(
                tareaSeleccionada = tarea,
                tiempoRestante = tiempoInicial,
                isTimerRunning = false,
                mostrarRecordatorioEstiramiento = false
            ) 
        }
    }

    fun toggleTimer() {
        val tarea = _uiState.value.tareaSeleccionada ?: return
        if (_uiState.value.isTimerRunning) {
            viewModelScope.launch {
                pausarTareaUseCase(tarea.id).onSuccess { mensaje ->
                    stopTimer()
                    sessionManager.saveTaskProgress(tarea.id, _uiState.value.tiempoRestante)
                    _uiState.update { it.copy(successMessage = mensaje) }
                    loadTareas()
                }.onFailure { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
            }
        } else {
            viewModelScope.launch {
                // Si la tarea está en estado Pendiente (1), avisamos inicio al servidor
                if (tarea.idEstado == 1) {
                    iniciarTareaUseCase(tarea.id).onSuccess { mensaje ->
                        _uiState.update { it.copy(successMessage = mensaje) }
                        startTimer()
                        loadTareas()
                    }.onFailure { error ->
                        _uiState.update { it.copy(error = error.message) }
                    }
                } else {
                    // Si ya estaba en progreso (3) o pausada localmente, solo reanudamos el reloj
                    startTimer()
                }
            }
        }
    }

    fun completarTarea() {
        val tarea = _uiState.value.tareaSeleccionada ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, mostrarSheetCompletado = false) }
            completarTareaUseCase(tarea.id).onSuccess { mensaje ->
                sessionManager.clearTaskProgress(tarea.id)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        mostrarMensajeExito = true,
                        mensajeExito = mensaje,
                        tareaSeleccionada = null
                    )
                }
                loadTareas()
            }.onFailure { error -> _uiState.update { it.copy(error = error.message, isLoading = false) } }
        }
    }

    fun eliminarTarea(idTarea: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            eliminarTareaUseCase(idTarea).onSuccess { mensaje ->
                sessionManager.clearTaskProgress(idTarea) // Limpiar progreso al borrar
                _uiState.update { it.copy(successMessage = mensaje, isLoading = false) }
                if (_uiState.value.tareaSeleccionada?.id == idTarea) {
                    stopTimer()
                    _uiState.update { it.copy(tareaSeleccionada = null, tiempoRestante = 0) }
                }
                loadTareas()
            }.onFailure { error -> _uiState.update { it.copy(error = error.message, isLoading = false) } }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }

    // Funciones auxiliares...
    fun descartarRecordatorio() { _uiState.update { it.copy(mostrarRecordatorioEstiramiento = false) }; segundosTranscurridos = 0 }
    fun mostrarSheetNuevaTarea(mostrar: Boolean) { _uiState.update { it.copy(mostrarSheetNuevaTarea = mostrar) } }
    fun onTituloCambiado(titulo: String) { _uiState.update { it.copy(nuevoTitulo = titulo) } }
    fun onCategoriaSeleccionada(nombre: String) { _uiState.update { it.copy(nuevaCategoriaNombre = nombre) } }
    fun onDuracionCambiada(minutos: Int) { _uiState.update { it.copy(nuevaDuracion = minutos, mostrarSelectorDuracion = false) } }
    fun onNumeroPresionado(numero: String) { _uiState.update { s -> s.copy(nuevaDuracionInput = (s.nuevaDuracionInput + numero).takeLast(4)) } }
    fun onBorrarPresionado() { _uiState.update { s -> s.copy(nuevaDuracionInput = ("0" + s.nuevaDuracionInput.dropLast(1)).takeLast(4)) } }
    fun confirmarDuracion() {
        val s = _uiState.value
        val total = ((s.nuevaDuracionInput.substring(0, 2).toIntOrNull() ?: 0) * 60) +
            (s.nuevaDuracionInput.substring(2, 4).toIntOrNull() ?: 0)
        val minutos = total.coerceAtLeast(20)
        _uiState.update {
            it.copy(
                nuevaDuracion = minutos,
                mostrarSelectorDuracion = false,
                nuevaDuracionInput = "0000"
            )
        }
    }
    fun mostrarSelectorDuracion(m: Boolean) { _uiState.update { it.copy(mostrarSelectorDuracion = m, nuevaDuracionInput = "0000") } }
    fun mostrarSheetCompletado(m: Boolean) { _uiState.update { it.copy(mostrarSheetCompletado = m) } }
    fun mostrarSheetMasTiempo(m: Boolean) { _uiState.update { it.copy(mostrarSheetMasTiempo = m, mostrarSheetCompletado = !m) } }
    fun actualizarTiempoAdicional(m: Int) { _uiState.update { it.copy(tiempoAdicional = m.coerceIn(15, 120)) } }
    fun descartarMensajeExito() { _uiState.update { it.copy(mostrarMensajeExito = false) } }

    fun descartarAlertaSalud() {
        _uiState.update { it.copy(mostrarAlertaSalud = false) }
        segundosParaAlerta = 0
    }

    fun agregarMasTiempo(m: Int) { _uiState.update { it.copy(mostrarSheetMasTiempo = false, tiempoRestante = m * 60, isTimerRunning = true, tiempoAdicional = m) }; startTimer() }

    fun agregarTarea() {
        val currentState = _uiState.value
        val titulo = currentState.nuevoTitulo.trim()
        if (titulo.isBlank()) {
            _uiState.update { it.copy(error = "El título de la tarea es obligatorio.") }
            return
        }
        if (currentState.nuevaDuracion < 20) {
            _uiState.update { it.copy(error = "La duración mínima es de 20 minutos.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, mostrarSheetNuevaTarea = false) }
            createTareaUseCase(titulo, currentState.nuevaCategoriaNombre, currentState.nuevaDuracion).onSuccess { mensaje ->
                _uiState.update { it.copy(successMessage = mensaje, nuevoTitulo = "", nuevaDuracion = 45, isLoading = false) }
                loadTareas()
            }.onFailure { error -> _uiState.update { it.copy(error = error.message, isLoading = false) } }
        }
    }

    private fun startTimer() {
        if (_uiState.value.tiempoRestante <= 0) return
        timerJob?.cancel()
        
        // --- PROGRAMAR ALARMA DE SISTEMA (30 MIN) ---
        _uiState.value.tareaSeleccionada?.let { tarea ->
            programarAlarmaAlerta(tarea.id)
        }
        // --------------------------------------------

        _uiState.update { it.copy(isTimerRunning = true) }
        timerJob = viewModelScope.launch {
            while (_uiState.value.tiempoRestante > 0) {
                delay(1000)
                segundosTranscurridos++
                segundosParaAlerta++

                // Alerta básica de estiramiento (25 min)
                if (segundosTranscurridos >= INTERVALO_ESTIRAMIENTO) {
                    _uiState.update { it.copy(mostrarRecordatorioEstiramiento = true) }
                }

                // Alerta de API (30 min)
                if (segundosParaAlerta >= INTERVALO_ALERTA_API) {
                    _uiState.value.tareaSeleccionada?.let { tarea ->
                        getAlertaSaludUseCase(tarea.id).onSuccess { info ->
                            _uiState.update { it.copy(mostrarAlertaSalud = true, alertaSaludInfo = info) }
                            // TODO: Disparar notificación de sistema aquí
                        }
                    }
                }
                
                val nuevoTiempo = _uiState.value.tiempoRestante - 1
                _uiState.update { it.copy(tiempoRestante = nuevoTiempo) }
                
                // Guardar progreso cada 10 segundos para no saturar memoria, o al pausar
                if (nuevoTiempo % 10 == 0) {
                    _uiState.value.tareaSeleccionada?.let { sessionManager.saveTaskProgress(it.id, nuevoTiempo) }
                }
            }
            _uiState.update { it.copy(isTimerRunning = false, mostrarSheetCompletado = true) }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        
        // --- CANCELAR ALARMA AL PAUSAR ---
        _uiState.value.tareaSeleccionada?.let { cancelarAlarmaAlerta(it.id) }
        // ---------------------------------

        _uiState.update { it.copy(isTimerRunning = false) }
        _uiState.value.tareaSeleccionada?.let { sessionManager.saveTaskProgress(it.id, _uiState.value.tiempoRestante) }
    }

    private fun programarAlarmaAlerta(idTarea: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlertaSaludReceiver::class.java).apply {
            putExtra("idTarea", idTarea)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, idTarea, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Programar para dentro de 30 minutos (1800 segundos)
        val triggerTime = System.currentTimeMillis() + (30 * 60 * 1000)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    }

    private fun cancelarAlarmaAlerta(idTarea: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlertaSaludReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, idTarea, intent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }
}
