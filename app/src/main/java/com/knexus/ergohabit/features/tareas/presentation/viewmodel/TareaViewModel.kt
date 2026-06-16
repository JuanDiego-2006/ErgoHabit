package com.knexus.ergohabit.features.tareas.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knexus.ergohabit.features.tareas.domain.usecases.GetTareasUseCase
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
    private val createTareaUseCase: com.knexus.ergohabit.features.tareas.domain.usecases.CreateTareaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TareaUiState())
    val uiState: StateFlow<TareaUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var segundosTranscurridos = 0
    private val INTERVALO_ESTIRAMIENTO = 25 * 60

    init {
        // Por ahora usamos un ID de usuario fijo para pruebas (ej: 1)
        loadTareas(1)
    }

    fun loadTareas(idUsuario: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getTareasUseCase(idUsuario).collect { result ->
                result.onSuccess { tareas ->
                    _uiState.update { it.copy(tareas = tareas, isLoading = false) }
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
                tiempoRestante = tarea.duracionTarea * 60,
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
        segundosTranscurridos = 0 // Reiniciamos el contador para el próximo recordatorio
    }

    fun mostrarSheetNuevaTarea(mostrar: Boolean) {
        _uiState.update { it.copy(mostrarSheetNuevaTarea = mostrar) }
    }

    fun onTituloCambiado(titulo: String) {
        _uiState.update { it.copy(nuevoTitulo = titulo) }
    }

    fun onCategoriaSeleccionada(id: Int) {
        _uiState.update { it.copy(nuevaCategoriaId = id) }
    }

    fun onDuracionCambiada(minutos: Int) {
        _uiState.update { it.copy(nuevaDuracion = minutos, mostrarSelectorDuracion = false) }
    }

    fun onNumeroPresionado(numero: String) {
        _uiState.update { state ->
            val actual = state.nuevaDuracionInput
            // Solo permitimos 4 dígitos
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

    fun agregarTarea() {
        val currentState = _uiState.value
        val nuevaTarea = com.knexus.ergohabit.features.tareas.domain.entities.TareaEnfoque(
            id = 0, // El servidor debería generar el ID
            idUsuario = 1, // ID fijo por ahora
            idEstado = 1, // Pendiente
            titulo = currentState.nuevoTitulo,
            duracionTarea = currentState.nuevaDuracion,
            idCategoria = currentState.nuevaCategoriaId,
            fechaCreacion = "" // El servidor debería manejar esto o podemos enviar la actual
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, mostrarSheetNuevaTarea = false) }
            createTareaUseCase(nuevaTarea).collect { result ->
                result.onSuccess {
                    loadTareas(1) // Recargar la lista
                    _uiState.update { it.copy(nuevoTitulo = "", nuevaDuracion = 45) }
                }.onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
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
            _uiState.update { it.copy(isTimerRunning = false) }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(isTimerRunning = false) }
    }
}
