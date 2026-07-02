package com.knexus.ergohabit.features.posture.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knexus.ergohabit.features.posture.domain.usecase.GetNutricionDashboardUseCase
import com.knexus.ergohabit.features.posture.domain.usecase.MarcarComidaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NutricionViewModel @Inject constructor(
    private val getNutricionDashboardUseCase: GetNutricionDashboardUseCase,
    private val marcarComidaUseCase: MarcarComidaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NutricionUiState())
    val uiState: StateFlow<NutricionUiState> = _uiState.asStateFlow()

    init {
        // En init, cargamos de forma silenciosa si ya tenemos algo para evitar parpadeo
        cargarDashboard(silent = true)
        startAutoRefresh()
    }

    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                delay(30_000)
                cargarDashboard(silent = true)
            }
        }
    }

    fun cargarDashboard(silent: Boolean = false) {
        viewModelScope.launch {
            // Solo mostramos loading si es la primerísima vez (estado vacío)
            if (!silent && _uiState.value.horaDesayuno == "--:--") {
                _uiState.update { it.copy(isLoading = true) }
            }
            
            getNutricionDashboardUseCase().collect { result ->
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
            _uiState.update { current ->
                when(tipoComida) {
                    "DESAYUNO" -> current.copy(desayunoCompletado = completada)
                    "COMIDA" -> current.copy(comidaCompletada = completada)
                    "CENA" -> current.copy(cenaCompletada = completada)
                    else -> current
                }
            }
            
            marcarComidaUseCase(tipoComida, completada).onSuccess { msg ->
                _uiState.update { it.copy(successMessage = msg) }
                cargarDashboard(silent = true)
            }.onFailure { error ->
                _uiState.update { it.copy(error = error.message) }
                cargarDashboard(silent = true)
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, error = null) }
    }
}
