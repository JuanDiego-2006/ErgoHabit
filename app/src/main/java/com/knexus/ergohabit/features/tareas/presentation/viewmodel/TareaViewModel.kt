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
    private var idTareaEnEjecucion: Int? = null
    private var segundosTranscurridos = 0
    private var segundosParaAlertaHealth = 0
    private val INTERVALO_ALERTA_API = 30 * 60
    private val INTERVALO_ESTIRAMIENTO = 30 * 60

    init {
        loadTareas()
        startAutoRefresh()
    }

    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                delay(30_000) // Actualizar cada 30 segundos de forma silenciosa
                loadTareas()
            }
        }
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
            // Solo mostramos Loading si no tenemos datos previos
            if (_uiState.value.tareasEstado == null) {
                _uiState.update { it.copy(isLoading = true) }
            }

            getTareasUseCase().collect { result ->
                result.onSuccess { estado ->
                    var tareaParaSincronizar: TareaEnfoque? = null

                    _uiState.update { current ->
                        val actual = current.tareaSeleccionada

                        // 1. Sincronizar estado visual de cronómetro con la lista remota
                        val pendientesSincronizadas = estado.pendientes.map { tarea ->
                            if (tarea.id == idTareaEnEjecucion && current.isTimerRunning) {
                                tarea.copy(fechaInicioCronometro = tarea.fechaInicioCronometro ?: "ACTIVO_LOCAL")
                            } else tarea
                        }
                        val estadoSincronizado = estado.copy(pendientes = pendientesSincronizadas)

                        // 2. Buscamos la versión actualizada de la tarea seleccionada
                        val encontrada = (estadoSincronizado.pendientes + estadoSincronizado.completadas)
                            .find { it.id == actual?.id }
                        val nuevaSeleccionada = encontrada ?: actual

                        // 3. Sincronización si es necesario
                        if (nuevaSeleccionada?.fechaInicioCronometro != null && idTareaEnEjecucion == null) {
                            tareaParaSincronizar = nuevaSeleccionada
                        }

                        // --- ESCUDO DE LISTA ESTÁTICA ---
                        val setIdsActuales = current.tareasEstado?.pendientes?.map { it.id }?.toSet() ?: emptySet()
                        val setIdsNuevos = estadoSincronizado.pendientes.map { it.id }.toSet()

                        val listaFinal = if (setIdsActuales == setIdsNuevos && current.tareasEstado != null) {
                            val pendientesManteniendoOrden = current.tareasEstado!!.pendientes.mapNotNull { local ->
                                estadoSincronizado.pendientes.find { it.id == local.id }
                            }
                            current.tareasEstado!!.copy(
                                pendientes = pendientesManteniendoOrden,
                                completadas = estadoSincronizado.completadas,
                                totalPendientesText = estadoSincronizado.totalPendientesText,
                                totalCompletadasText = estadoSincronizado.totalCompletadasText
                            )
                        } else {
                            estadoSincronizado
                        }

                        // --- ACTUALIZACIÓN DE TIEMPO INTELIGENTE ---
                        // Si la tarea seleccionada no está corriendo, actualizamos su tiempo restante
                        val nuevoTiempo = if (idTareaEnEjecucion != nuevaSeleccionada?.id) {
                            val segundosAPI = (nuevaSeleccionada?.duracionMinutos ?: 0) * 60
                            // Si la diferencia es menor a 65 segundos, conservamos los segundos locales
                            // para evitar que el cronómetro "salte" al minuto entero al pausar.
                            if (nuevaSeleccionada?.id == actual?.id && Math.abs(current.tiempoRestante - segundosAPI) < 65) {
                                current.tiempoRestante
                            } else {
                                segundosAPI
                            }
                        } else {
                            current.tiempoRestante
                        }

                        current.copy(
                            tareasEstado = listaFinal,
                            isLoading = false,
                            tareaSeleccionada = nuevaSeleccionada,
                            tiempoRestante = nuevoTiempo
                        )
                    }

                    tareaParaSincronizar?.let { recuperarYIniciarSincronizado(it) }

                }.onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
            }
        }
    }

    private fun recuperarYIniciarSincronizado(tarea: TareaEnfoque) {
        viewModelScope.launch {
            // 1. Obtener detalles del cronómetro (frases/acciones) según flujo API
            getAlertaSaludUseCase(tarea.id).onSuccess { info ->
                _uiState.update { it.copy(alertaSaludInfo = info) }
            }

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
            val targetEnd = progreso?.targetEndTimeMs ?: (ahora + (segundos * 1000L))

            idTareaEnEjecucion = tarea.id

            _uiState.update {
                it.copy(
                    tareaSeleccionada = tarea,
                    tiempoRestante = segundos,
                    duracionSesionActual = inicial,
                    targetEndTimeMs = targetEnd,
                    isTimerRunning = (tarea.fechaInicioCronometro != null && segundos > 0)
                )
            }
            if (_uiState.value.isTimerRunning) startTimerVisual()
        }
    }

    fun seleccionarTarea(tarea: TareaEnfoque) {
        if (_uiState.value.tareaSeleccionada?.id == tarea.id) return
        viewModelScope.launch {
            // Guardamos el progreso de la anterior pero NO cancelamos el job global si ya hay uno corriendo
            _uiState.value.tareaSeleccionada?.let { anterior ->
                saveLocalProgressUseCase(
                    anterior.id,
                    _uiState.value.tiempoRestante,
                    _uiState.value.duracionSesionActual,
                    if (idTareaEnEjecucion == anterior.id) _uiState.value.targetEndTimeMs else -1L
                )
            }

            // --- FLUJO API: Preparar la pantalla del cronómetro ---
            getAlertaSaludUseCase(tarea.id).onSuccess { info ->
                _uiState.update { it.copy(alertaSaludInfo = info) }
            }

            val progreso = getLocalProgressUseCase(tarea.id)
            val ahora = System.currentTimeMillis()
            val segundosAPI = tarea.duracionMinutos * 60

            val segundos = if (idTareaEnEjecucion == tarea.id && progreso != null && progreso.targetEndTimeMs > ahora) {
                ((progreso.targetEndTimeMs - ahora) / 1000).toInt()
            } else if (progreso != null && Math.abs(progreso.segundosRestantes - segundosAPI) < 65) {
                progreso.segundosRestantes
            } else {
                segundosAPI
            }

            val inicial = progreso?.segundosIniciales ?: (tarea.duracionMinutos * 60)
            val runningThisOne = idTareaEnEjecucion == tarea.id

            _uiState.update {
                it.copy(
                    tareaSeleccionada = tarea,
                    tiempoRestante = segundos,
                    duracionSesionActual = inicial,
                    targetEndTimeMs = if (runningThisOne) (progreso?.targetEndTimeMs ?: -1L) else -1L,
                    isTimerRunning = (runningThisOne && segundos > 0)
                )
            }
            // Si la tarea que seleccionamos es la que estaba en ejecución y el job se perdió por alguna razón, lo relanzamos
            if (runningThisOne && timerJob?.isActive != true && segundos > 0) startTimerVisual()
        }
    }

    fun toggleTimer() {
        val tarea = _uiState.value.tareaSeleccionada ?: return
        if (_uiState.value.isTimerRunning) {
            viewModelScope.launch {
                pausarTareaUseCase(tarea.id).onSuccess { mensaje ->
                    timerJob?.cancel()
                    idTareaEnEjecucion = null
                    _uiState.update { it.copy(isTimerRunning = false, targetEndTimeMs = -1L) }
                    cancelarTodasLasAlarmas(tarea.id)
                    saveLocalProgressUseCase(tarea.id, _uiState.value.tiempoRestante, _uiState.value.duracionSesionActual, -1L)
                    _uiState.update { it.copy(successMessage = mensaje) }
                    loadTareas()
                }.onFailure { error -> _uiState.update { it.copy(error = error.message) } }
            }
        } else {
            viewModelScope.launch {
                // ASEGURAR INFO DE ALERTA: Si no la tenemos, la pedimos antes de iniciar
                val infoAlerta = _uiState.value.alertaSaludInfo ?: getAlertaSaludUseCase(tarea.id).getOrNull()

                iniciarTareaUseCase(tarea.id).onSuccess { mensaje ->
                    idTareaEnEjecucion = tarea.id
                    val duration = _uiState.value.tiempoRestante
                    val targetEnd = System.currentTimeMillis() + (duration.toLong() * 1000)
                    val inicial = if (_uiState.value.duracionSesionActual > 0) _uiState.value.duracionSesionActual else duration

                    _uiState.update { it.copy(
                        successMessage = mensaje,
                        duracionSesionActual = inicial,
                        targetEndTimeMs = targetEnd,
                        isTimerRunning = true,
                        alertaSaludInfo = infoAlerta
                    ) }
                    saveLocalProgressUseCase(tarea.id, duration, inicial, targetEnd)

                    // --- FLUJO API: Programar alertas de salud si el servidor lo autorizó ---
                    if (infoAlerta?.requierePostura == true) {
                        programarAlarmaSistema(tarea.id, (30 * 60).toLong(), "ALERTA_SALUD")
                    }

                    programarAlarmaSistema(tarea.id, duration.toLong(), "FIN_TAREA")
                    startTimerVisual()
                    loadTareas()
                }.onFailure { error -> _uiState.update { it.copy(error = error.message) } }
            }
        }
    }

    private fun startTimerVisual() {
        val runningId = idTareaEnEjecucion ?: return
        timerJob?.cancel()

        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                segundosParaAlertaHealth++

                val state = _uiState.value
                val ahora = System.currentTimeMillis()

                // --- FLUJO API: Solo mostrar alerta si requierePostura es true ---
                if (segundosParaAlertaHealth >= INTERVALO_ALERTA_API && state.alertaSaludInfo?.requierePostura == true) {
                    // Pausa automática por salud
                    timerJob?.cancel()
                    _uiState.update { it.copy(isTimerRunning = false, mostrarAlertaSalud = true) }
                    pausarTareaUseCase(runningId)
                    segundosParaAlertaHealth = 0
                    break // Salimos del bucle visual ya que está pausado
                }

                // Obtener el fin de la tarea que corre (del estado si es la seleccionada, sino de Room/Variable)
                val targetEnd = if (state.tareaSeleccionada?.id == runningId) {
                    state.targetEndTimeMs
                } else {
                    getLocalProgressUseCase(runningId)?.targetEndTimeMs ?: -1L
                }

                if (targetEnd > 0) {
                    val nuevoRestante = ((targetEnd - ahora) / 1000).toInt()

                    if (nuevoRestante <= 0) {
                        finalizarTareaVisualmente(runningId)
                        break
                    }

                    // SOLO ACTUALIZAMOS EL TIEMPO EN UI SI LA TAREA SELECCIONADA ES LA QUE CORRE
                    if (state.tareaSeleccionada?.id == runningId) {
                        _uiState.update { it.copy(tiempoRestante = nuevoRestante) }
                    }
                }

                if (segundosParaAlertaHealth % 5 == 0) {
                    val currentRemaining = if (targetEnd > 0) ((targetEnd - ahora) / 1000).toInt() else 0
                    saveLocalProgressUseCase(runningId, currentRemaining, state.duracionSesionActual, targetEnd)
                }
            }
        }
    }

    private suspend fun finalizarTareaVisualmente(id: Int) {
        pausarTareaUseCase(id)
        cancelarTodasLasAlarmas(id)
        clearLocalProgressUseCase(id)
        if (_uiState.value.tareaSeleccionada?.id == id) {
            _uiState.update { it.copy(isTimerRunning = false, mostrarSheetCompletado = true, tiempoRestante = 0) }
        }
        idTareaEnEjecucion = null
    }

    fun completarTarea() {
        val t = _uiState.value.tareaSeleccionada ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, mostrarSheetCompletado = false) }
            // --- LIMPIEZA: Ahora llamamos directo a completar sin trucos de estados ---
            completarTareaUseCase(t.id).onSuccess { mensaje ->
                cancelarTodasLasAlarmas(t.id)
                clearLocalProgressUseCase(t.id)
                _uiState.update { it.copy(isLoading = false, mostrarMensajeExito = true, mensajeExito = mensaje, tareaSeleccionada = null, duracionSesionActual = 0, tiempoRestante = 0) }
                loadTareas()
            }.onFailure { error -> _uiState.update { it.copy(error = error.message, isLoading = false) } }
        }
    }

    fun extenderTarea(minutos: Int) {
        val t = _uiState.value.tareaSeleccionada ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, mostrarSheetMasTiempo = false) }
            // --- LIMPIEZA: Ahora llamamos directo a extender sin trucos de estados ---
            extenderTareaUseCase(t.id, minutos).onSuccess { mensaje ->
                val segundosExtras = minutos * 60
                _uiState.update { it.copy(successMessage = mensaje, isLoading = false, tiempoRestante = segundosExtras, duracionSesionActual = segundosExtras, isTimerRunning = false) }
                saveLocalProgressUseCase(t.id, segundosExtras, segundosExtras, -1L)
                loadTareas()
            }.onFailure { error -> _uiState.update { it.copy(error = error.message, isLoading = false) } }
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

    fun mostrarAlertaDesdeNotificacion(frase: String, accion: String, idTarea: Int = -1) {
        viewModelScope.launch {
            // 1. Pausa inmediata local y en servidor de la tarea que esté corriendo
            val tareaActivaId = idTarea.takeIf { it != -1 } ?: idTareaEnEjecucion
            tareaActivaId?.let { pausarTareaUseCase(it) }

            timerJob?.cancel()
            idTareaEnEjecucion = null
            _uiState.update { it.copy(isTimerRunning = false) }

            // 2. Seleccionar la tarea que generó la alerta para mostrarla en el cronómetro
            if (idTarea != -1) {
                seleccionarTareaPorId(idTarea)
            }

            // 3. Mostrar el diálogo de salud
            _uiState.update { it.copy(
                mostrarAlertaSalud = true,
                alertaSaludInfo = com.knexus.ergohabit.features.tareas.domain.entities.AlertaSalud(frase, accion, true)
            ) }
        }
    }

    fun seleccionarTareaPorId(id: Int) {
        viewModelScope.launch {
            val tareas = _uiState.value.tareasEstado?.pendientes ?: emptyList()
            val encontrada = tareas.find { it.id == id }
            if (encontrada != null) {
                seleccionarTarea(encontrada)
            } else {
                // Si no la encontramos (ej: lista cargando), forzamos una sola carga de la API
                getTareasUseCase().collect { result ->
                    result.onSuccess { estado ->
                        estado.pendientes.find { it.id == id }?.let {
                            seleccionarTarea(it)
                            return@collect
                        }
                    }
                }
            }
        }
    }

    fun clearMessages() { _uiState.update { it.copy(error = null, successMessage = null) } }
    fun descartarRecordatorio() { _uiState.update { it.copy(mostrarRecordatorioEstiramiento = false) }; segundosTranscurridos = 0 }

    fun descartarAlertaSalud() {
        _uiState.update { it.copy(mostrarAlertaSalud = false) }
        segundosParaAlertaHealth = 0
        // REANUDACIÓN AUTOMÁTICA: Al darle "¡Listo!", reanudamos la tarea en el servidor y el timer local
        toggleTimer()
    }
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
