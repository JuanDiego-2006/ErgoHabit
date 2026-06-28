package com.knexus.ergohabit.features.posture.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knexus.ergohabit.features.posture.data.datasource.api.HabitosApi
import com.knexus.ergohabit.features.posture.data.models.MarcarComidaRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NutricionViewModel @Inject constructor(
    private val api: HabitosApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(NutricionUiState())
    val uiState: StateFlow<NutricionUiState> = _uiState.asStateFlow()

    init {
        cargarDashboard()
    }

    private fun cargarDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val respuesta = api.obtenerDashboardNutricion()
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
            } catch (_: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun marcarComida(tipoComida: String, completada: Boolean) {
        viewModelScope.launch {
            try {
                api.marcarComida(MarcarComidaRequest(tipoComida = tipoComida, estado = completada))
                cargarDashboard()
            } catch (_: Exception) {
            }
        }
    }
}
