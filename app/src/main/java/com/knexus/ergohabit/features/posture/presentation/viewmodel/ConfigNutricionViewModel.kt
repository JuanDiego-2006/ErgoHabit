package com.knexus.ergohabit.features.posture.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ConfigNutricionViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ConfigNutricionUiState())
    val uiState: StateFlow<ConfigNutricionUiState> = _uiState.asStateFlow()

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
        // Aquí irá la lógica para guardar los horarios
    }
}