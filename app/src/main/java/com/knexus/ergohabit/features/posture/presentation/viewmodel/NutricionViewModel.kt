package com.knexus.ergohabit.features.posture.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knexus.ergohabit.features.posture.domain.repository.NutricionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NutricionViewModel @Inject constructor(
    private val repository: NutricionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NutricionUiState())
    val uiState: StateFlow<NutricionUiState> = _uiState.asStateFlow()

    init {
        cargarDashboard()
    }

    private fun cargarDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getNutricionDashboard().collect { result ->
                result.onSuccess { respuesta ->
                    val completadas = listOf(
                        respuesta.chequeoDesayuno,
                        respuesta.chequeoComida,
                        respuesta.chequeoCena
                    ).count { it }
                    _uiState.update {
                        it.copy(
                            comidasCompletadas = completadas,
                            comidasObjetivo = 3,
                            desayunoCompletado = respuesta.chequeoDesayuno,
                            comidaCompletada = respuesta.chequeoComida,
                            cenaCompletada = respuesta.chequeoCena,
                            horaDesayuno = respuesta.horaDesayunoConfigurada,
                            horaComida = respuesta.horaComidaConfigurada,
                            horaCena = respuesta.horaCenaConfigurada,
                            mensajeFaltante = respuesta.mensajeFaltanteText,
                            fraseMotivacional = respuesta.fraseMotivacional,
                            tips = respuesta.tipsNutricion,
                            porcentajeBackend = respuesta.porcentajeCumplimiento,
                            isLoading = false
                        )
                    }
                }.onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            }
        }
    }

    fun marcarComida(tipoComida: String, completada: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }
            repository.marcarComida(tipoComida, completada).onSuccess { msg ->
                _uiState.update { it.copy(isLoading = false, successMessage = msg) }
                cargarDashboard()
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, error = null) }
    }
}
