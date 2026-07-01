package com.knexus.ergohabit.features.posture.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knexus.ergohabit.features.posture.domain.repository.SuenoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SuenoViewModel @Inject constructor(
    private val repository: SuenoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SuenoUiState())
    val uiState: StateFlow<SuenoUiState> = _uiState.asStateFlow()

    init {
        cargarDashboard()
    }

    private fun cargarDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getSuenoDashboard().collect { result ->
                result.onSuccess { respuesta ->
                    _uiState.update {
                        it.copy(
                            horasDormidas = respuesta.horasDormidasReales.toFloat(),
                            horasRecomendadas = if (respuesta.horasPlanificadas > 0) 
                                                   respuesta.horasPlanificadas.toFloat() 
                                                else 8f,
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
                }.onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            }
        }
    }

    fun registrarDespertar() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }
            repository.registrarDespertar().onSuccess { msg ->
                _uiState.update { it.copy(mostrarAlarma = false, isLoading = false, successMessage = msg) }
                cargarDashboard()
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    fun mostrarAlarma(show: Boolean) {
        _uiState.update { it.copy(mostrarAlarma = show) }
    }

    fun posponerAlarma() {
        _uiState.update { it.copy(mostrarAlarma = false) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, error = null) }
    }
}
