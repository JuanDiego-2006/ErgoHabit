package com.knexus.ergohabit.features.posture.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HidratacionViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(HidratacionUiState())
    val uiState: StateFlow<HidratacionUiState> = _uiState.asStateFlow()

    fun agregarAgua(ml: Int) {
        _uiState.update { estado ->
            estado.copy(mlActuales = (estado.mlActuales + ml).coerceAtMost(estado.mlObjetivo))
        }
    }
}