package com.knexus.ergohabit.features.posture.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ConfigHorarioViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ConfigHorarioUiState())
    val uiState: StateFlow<ConfigHorarioUiState> = _uiState.asStateFlow()

    fun incrementarHoras() {
        _uiState.update { estado ->
            estado.copy(horas = (estado.horas + 1) % 24)
        }
    }

    fun decrementarHoras() {
        _uiState.update { estado ->
            estado.copy(horas = (estado.horas - 1 + 24) % 24)
        }
    }

    fun incrementarMinutos() {
        _uiState.update { estado ->
            estado.copy(minutos = (estado.minutos + 5) % 60)
        }
    }

    fun decrementarMinutos() {
        _uiState.update { estado ->
            estado.copy(minutos = (estado.minutos - 5 + 60) % 60)
        }
    }

    fun guardarHorario() {
        // Aquí irá la lógica para guardar el horario
    }
}