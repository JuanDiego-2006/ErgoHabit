package com.knexus.ergohabit.features.posture.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knexus.ergohabit.features.posture.data.datasource.api.HabitosApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SuenoViewModel @Inject constructor(
    private val api: HabitosApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(SuenoUiState())
    val uiState: StateFlow<SuenoUiState> = _uiState.asStateFlow()

    init {
        cargarDashboard()
    }

    private fun cargarDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val respuesta = api.obtenerDashboardSueno()
                _uiState.update {
                    it.copy(
                        horasDormidas = respuesta.horasDormidasReales.toFloat(),
                        horasRecomendadas = respuesta.horasPlanificadas.toFloat(),
                        calidad = when {
                            respuesta.porcentajeCumplimiento >= 80 -> "Buena"
                            respuesta.porcentajeCumplimiento >= 50 -> "Moderada"
                            else -> "Baja"
                        },
                        horaDormir = respuesta.horaDormirConfigurada,
                        horaDespertar = respuesta.horaDespertarConfigurada,
                        alarmaActivada = respuesta.despertoATiempo,
                        fraseMotivacional = respuesta.fraseMotivacional,
                        tips = respuesta.tipsSueno,
                        isLoading = false
                    )
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun registrarDespertar() {
        viewModelScope.launch {
            try {
                api.registrarDespertarSueno()
                cargarDashboard()
            } catch (_: Exception) {
            }
        }
    }
}
