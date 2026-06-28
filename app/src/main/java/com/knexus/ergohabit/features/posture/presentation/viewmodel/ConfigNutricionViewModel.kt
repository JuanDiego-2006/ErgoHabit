package com.knexus.ergohabit.features.posture.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knexus.ergohabit.features.posture.data.datasource.api.HabitosApi
import com.knexus.ergohabit.features.posture.data.models.ConfigurarNutricionRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigNutricionViewModel @Inject constructor(
    private val api: HabitosApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConfigNutricionUiState())
    val uiState: StateFlow<ConfigNutricionUiState> = _uiState.asStateFlow()

    init {
        cargarHorarios()
    }

    private fun cargarHorarios() {
        viewModelScope.launch {
            try {
                val dashboard = api.obtenerDashboardNutricion()
                _uiState.update {
                    it.copy(
                        horaDesayuno = dashboard.horaDesayunoConfigurada,
                        horaComida = dashboard.horaComidaConfigurada,
                        horaCena = dashboard.horaCenaConfigurada
                    )
                }
            } catch (_: Exception) {
            }
        }
    }

    fun onHoraDesayunoChange(hora: String) {
        _uiState.update { estado -> estado.copy(horaDesayuno = hora) }
    }

    fun onHoraComidaChange(hora: String) {
        _uiState.update { estado -> estado.copy(horaComida = hora) }
    }

    fun onHoraCenaChange(hora: String) {
        _uiState.update { estado -> estado.copy(horaCena = hora) }
    }

    fun guardar() {
        viewModelScope.launch {
            val estado = _uiState.value
            try {
                api.configurarHorariosNutricion(
                    ConfigurarNutricionRequest(
                        horaDesayuno = estado.horaDesayuno,
                        horaComida = estado.horaComida,
                        horaCena = estado.horaCena
                    )
                )
            } catch (_: Exception) {
            }
        }
    }
}
