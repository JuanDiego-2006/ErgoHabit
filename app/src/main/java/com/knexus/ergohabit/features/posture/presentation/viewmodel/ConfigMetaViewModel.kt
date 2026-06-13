package com.knexus.ergohabit.features.posture.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ConfigMetaViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ConfigMetaUiState())
    val uiState: StateFlow<ConfigMetaUiState> = _uiState.asStateFlow()

    fun seleccionarMeta(km: Float) {
        _uiState.update { estado -> estado.copy(metaSeleccionada = km) }
    }

    fun guardarMeta() {
        _uiState.update { estado -> estado.copy(metaActual = estado.metaSeleccionada) }
    }
}