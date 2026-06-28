package com.knexus.ergohabit.features.posture.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knexus.ergohabit.features.posture.data.datasource.api.HabitosApi
import com.knexus.ergohabit.features.posture.data.models.SuenoRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigHorarioViewModel @Inject constructor(
    private val api: HabitosApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConfigHorarioUiState())
    val uiState: StateFlow<ConfigHorarioUiState> = _uiState.asStateFlow()

    init {
        cargarHorarioActual()
    }

    private fun cargarHorarioActual() {
        viewModelScope.launch {
            try {
                val dashboard = api.obtenerDashboardSueno()
                val partes = dashboard.horaDespertarConfigurada.split(":")
                val horas = partes.getOrNull(0)?.toIntOrNull() ?: 6
                val minutos = partes.getOrNull(1)?.toIntOrNull() ?: 0
                _uiState.update { it.copy(horas = horas, minutos = minutos) }
            } catch (_: Exception) {
            }
        }
    }

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
        viewModelScope.launch {
            val estado = _uiState.value
            try {
                api.configurarHorarioSueno(
                    SuenoRequest(
                        horaDespertar = estado.horaDespertar,
                        horaDormir = estado.horaDormir
                    )
                )
            } catch (_: Exception) {
            }
        }
    }
}
