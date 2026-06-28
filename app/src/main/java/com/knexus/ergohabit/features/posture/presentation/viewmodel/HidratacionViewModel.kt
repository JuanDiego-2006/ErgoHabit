package com.knexus.ergohabit.features.posture.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knexus.ergohabit.features.posture.data.datasource.api.HabitosApi
import com.knexus.ergohabit.features.posture.data.models.RegistrarTomaRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HidratacionViewModel @Inject constructor(
    private val api: HabitosApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(HidratacionUiState())
    val uiState: StateFlow<HidratacionUiState> = _uiState.asStateFlow()

    init {
        cargarDashboardReal()
    }

    private fun cargarDashboardReal() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val respuesta = api.obtenerDashboardAgua()
                val consumido = if (respuesta.historialHoy.isNotEmpty()) {
                    respuesta.historialHoy.sumOf { it.cantidadMl }
                } else {
                    respuesta.consumidoHoyMl
                }
                val meta = respuesta.metaDiariaMl.coerceAtLeast(1)

                _uiState.update {
                    it.copy(
                        mlActuales = consumido,
                        mlObjetivo = meta,
                        vasosObjetivo = (meta / 250.0).toInt().coerceAtLeast(1),
                        fraseMotivacional = respuesta.fraseMotivacional,
                        tips = respuesta.tipsHidratacion,
                        isLoading = false,
                        isRegistrando = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "No se pudo cargar el dashboard") }
            }
        }
    }

    fun agregarAgua(cantidadMl: Int) = registrarVasoDeAgua(cantidadMl)

    fun registrarVasoDeAgua(cantidadMl: Int) {
        if (_uiState.value.isRegistrando) return
        viewModelScope.launch {
            _uiState.update { it.copy(isRegistrando = true, error = null) }
            try {
                api.registrarTomaAgua(RegistrarTomaRequest(cantidadMl = cantidadMl))
                cargarDashboardReal()
            } catch (e: Exception) {
                _uiState.update { it.copy(isRegistrando = false, error = "No se pudo registrar la toma") }
            }
        }
    }
}
