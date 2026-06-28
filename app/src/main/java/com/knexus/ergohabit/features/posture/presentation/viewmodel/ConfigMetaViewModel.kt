package com.knexus.ergohabit.features.posture.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knexus.ergohabit.features.posture.data.datasource.api.HabitosApi
import com.knexus.ergohabit.features.posture.data.models.HabitosMetaEjercicioRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigMetaViewModel @Inject constructor(
    private val api: HabitosApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConfigMetaUiState())
    val uiState: StateFlow<ConfigMetaUiState> = _uiState.asStateFlow()

    init {
        cargarMetaActual()
    }

    private fun cargarMetaActual() {
        viewModelScope.launch {
            try {
                val dashboard = api.obtenerDashboardEjercicio()
                val meta = dashboard.metaKmText
                    .replace(" km", "")
                    .replace(",", ".")
                    .trim()
                    .toFloatOrNull() ?: 8f
                _uiState.update { it.copy(metaSeleccionada = meta, metaActual = meta) }
            } catch (_: Exception) {
            }
        }
    }

    fun seleccionarMeta(km: Float) {
        _uiState.update { estado -> estado.copy(metaSeleccionada = km) }
    }

    fun guardarMeta() {
        viewModelScope.launch {
            val meta = _uiState.value.metaSeleccionada.toDouble()
            try {
                api.configurarMetaEjercicio(HabitosMetaEjercicioRequest(nuevaMeta = meta))
                _uiState.update { it.copy(metaActual = it.metaSeleccionada) }
            } catch (_: Exception) {
            }
        }
    }
}
