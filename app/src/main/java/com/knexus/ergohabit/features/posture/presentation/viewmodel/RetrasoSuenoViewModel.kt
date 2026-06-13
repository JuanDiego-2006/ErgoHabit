package com.knexus.ergohabit.features.posture.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RetrasoSuenoViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(RetrasoSuenoUiState())
    val uiState: StateFlow<RetrasoSuenoUiState> = _uiState.asStateFlow()

    fun incrementarHoras() {
        _uiState.update { estado ->
            estado.copy(horasRetraso = (estado.horasRetraso + 1).coerceAtMost(estado.horasActuales - 1))
        }
    }

    fun decrementarHoras() {
        _uiState.update { estado ->
            estado.copy(horasRetraso = (estado.horasRetraso - 1).coerceAtLeast(1))
        }
    }

    fun confirmar() {
        // Aquí irá la lógica para guardar el retraso
    }
}