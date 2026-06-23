package com.knexus.ergohabit.features.tareas.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knexus.ergohabit.features.tareas.domain.usecases.GetTareasUseCase
import com.knexus.ergohabit.features.tareas.domain.usecases.CreateTareaUseCase
import com.knexus.ergohabit.features.tareas.domain.usecases.CompletarTareaUseCase
import com.knexus.ergohabit.features.tareas.domain.usecases.GetMensajeExitoUseCase
import com.knexus.ergohabit.features.tareas.presentation.screens.TareaUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TareaViewModel @Inject constructor(
    private val getTareasUseCase: GetTareasUseCase,
    private val createTareaUseCase: CreateTareaUseCase,
    private val completarTareaUseCase: CompletarTareaUseCase,
    private val getMensajeExitoUseCase: GetMensajeExitoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TareaUiState())
    val uiState: StateFlow<TareaUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var segundosTranscurridos = 0
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
                }.onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
            }
        }
    }

    fun seleccionarTarea(tarea: com.knexus.ergohabit.features.tareas.domain.entities.TareaEnfoque) {
        segundosTranscurridos = 0
        _uiState.update { 
            it.copy(
                tareaSeleccionada = tarea,
                tiempoRestante = tarea.duracionMinutos * 60,
                isTimerRunning = false,
                mostrarRecordatorioEstiramiento = false
            ) 
        }
        stopTimer()
    }

    fun toggleTimer() {
        if (_uiState.value.isTimerRunning) {
            stopTimer()
        } else {
            startTimer()
        }
    }

    fun descartarRecordatorio() {
        _uiState.update { it.copy(mostrarRecordatorioEstiramiento = false) }
        segundosTranscurridos = 0
    }

    fun mostrarSheetNuevaTarea(mostrar: Boolean) {
        _uiState.update { it.copy(mostrarSheetNuevaTarea = mostrar) }
    }

    fun onTituloCambiado(titulo: String) {
        _uiState.update { it.copy(nuevoTitulo = titulo) }
    }

    fun onCategoriaSeleccionada(nombre: String) {
        _uiState.update { it.copy(nuevaCategoriaNombre = nombre) }
    }

    fun onDuracionCambiada(minutos: Int) {
        _uiState.update { it.copy(nuevaDuracion = minutos, mostrarSelectorDuracion = false) }
    }

    fun onNumeroPresionado(numero: String) {
        _uiState.update { state ->
            val actual = state.nuevaDuracionInput
            val nuevo = (actual + numero).takeLast(4)
            state.copy(nuevaDuracionInput = nuevo)
        }
    }

    fun onBorrarPresionado() {
        _uiState.update { state ->
            val actual = state.nuevaDuracionInput
            val nuevo = ("0" + actual.dropLast(1)).takeLast(4)
            state.copy(nuevaDuracionInput = nuevo)
        }
    }

    fun confirmarDuracion() {
        val state = _uiState.value
        val horas = state.nuevaDuracionInput.substring(0, 2).toIntOrNull() ?: 0
        val minutos = state.nuevaDuracionInput.substring(2, 4).toIntOrNull() ?: 0
        val totalMinutos = (horas * 60) + minutos
        
        _uiState.update { 
            it.copy(
                nuevaDuracion = if (totalMinutos > 0) totalMinutos else 1,
                mostrarSelectorDuracion = false,
                nuevaDuracionInput = "0000"
            )
        }
    }

    fun mostrarSelectorDuracion(mostrar: Boolean) {
        _uiState.update { it.copy(mostrarSelectorDuracion = mostrar, nuevaDuracionInput = "0000") }
    }

    fun mostrarSheetCompletado(mostrar: Boolean) {
        _uiState.update { it.copy(mostrarSheetCompletado = mostrar) }
    }

    fun mostrarSheetMasTiempo(mostrar: Boolean) {
        _uiState.update { it.copy(mostrarSheetMasTiempo = mostrar, mostrarSheetCompletado = !mostrar) }
    }

    fun actualizarTiempoAdicional(minutos: Int) {
        _uiState.update { it.copy(tiempoAdicional = minutos.coerceIn(15, 120)) }
    }

    fun completarTarea() {
        val tarea = _uiState.value.tareaSeleccionada ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, mostrarSheetCompletado = false) }
            completarTareaUseCase(tarea.id).onSuccess {
                val mensajeResult = getMensajeExitoUseCase()
                val mensaje = mensajeResult.getOrDefault("¡Tarea completada con éxito! 🌿")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        mostrarMensajeExito = true,
                        mensajeExito = mensaje,
                        tareaSeleccionada = null
                    )
                }
                loadTareas()
            }.onFailure { error ->
                _uiState.update { it.copy(error = error.message, isLoading = false) }
            }
        }
    }

    fun descartarMensajeExito() {
        _uiState.update { it.copy(mostrarMensajeExito = false) }
    }

    fun agregarMasTiempo(minutos: Int) {
        _uiState.update { 
            it.copy(
                mostrarSheetMasTiempo = false,
                tiempoRestante = minutos * 60,
                isTimerRunning = true,
                tiempoAdicional = minutos
            ) 
        }
        startTimer()
    }

    fun agregarTarea() {
        val currentState = _uiState.value
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, mostrarSheetNuevaTarea = false) }
            createTareaUseCase(
                titulo = currentState.nuevoTitulo,
                categoria = currentState.nuevaCategoriaNombre,
                duracionMinutos = currentState.nuevaDuracion
            ).onSuccess {
                loadTareas()
                _uiState.update { it.copy(nuevoTitulo = "", nuevaDuracion = 45) }
            }.onFailure { error ->
                _uiState.update { it.copy(error = error.message, isLoading = false) }
            }
        }
    }

    private fun startTimer() {
        if (_uiState.value.tiempoRestante <= 0) return
        
        _uiState.update { it.copy(isTimerRunning = true) }
        timerJob = viewModelScope.launch {
            while (_uiState.value.tiempoRestante > 0) {
                delay(1000)
                segundosTranscurridos++
                
                if (segundosTranscurridos >= INTERVALO_ESTIRAMIENTO) {
                    _uiState.update { it.copy(mostrarRecordatorioEstiramiento = true) }
                }

                _uiState.update { it.copy(tiempoRestante = it.tiempoRestante - 1) }
            }
            _uiState.update { it.copy(isTimerRunning = false, mostrarSheetCompletado = true) }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(isTimerRunning = false) }
    }
}
