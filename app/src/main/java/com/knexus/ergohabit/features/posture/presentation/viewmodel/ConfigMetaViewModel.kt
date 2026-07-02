package com.knexus.ergohabit.features.posture.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knexus.ergohabit.features.posture.domain.repository.EjercicioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigMetaViewModel @Inject constructor(
    private val repository: EjercicioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConfigMetaUiState())
    val uiState: StateFlow<ConfigMetaUiState> = _uiState.asStateFlow()

    init {
        cargarMetaActual()
    }

    private fun cargarMetaActual() {
        viewModelScope.launch {
            repository.getDashboardEjercicio().collect { result ->
                result.onSuccess { dashboard ->
                    val meta = dashboard.metaKmText
                        .replace("de ", "", ignoreCase = true)
                        .replace(" km", "", ignoreCase = true)
                        .replace(",", ".")
                        .trim()
                        .toFloatOrNull() ?: 8f
                    _uiState.update { it.copy(metaSeleccionada = meta, metaActual = meta) }
                }
            }
        }
    }

    fun seleccionarMeta(km: Float) {
        _uiState.update { estado -> estado.copy(metaSeleccionada = km) }
    }

    fun guardarMeta() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }
            val meta = _uiState.value.metaSeleccionada.toDouble()
            repository.configurarMeta(meta)
                .onSuccess { mensaje ->
                    _uiState.update { it.copy(
                        isLoading = false, 
                        metaActual = it.metaSeleccionada,
                        successMessage = mensaje 
                    ) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(
                        isLoading = false, 
                        error = error.message ?: "Error al guardar la meta" 
                    ) }
                }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, error = null) }
    }
}
