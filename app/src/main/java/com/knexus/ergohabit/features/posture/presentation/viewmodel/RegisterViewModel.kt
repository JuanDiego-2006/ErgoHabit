package com.knexus.ergohabit.features.posture.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNombreChange(value: String) {
        _uiState.update { estado -> estado.copy(nombre = value) }
    }

    fun onPrimerApellidoChange(value: String) {
        _uiState.update { estado -> estado.copy(primerApellido = value) }
    }

    fun onSegundoApellidoChange(value: String) {
        _uiState.update { estado -> estado.copy(segundoApellido = value) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { estado -> estado.copy(email = value) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { estado -> estado.copy(password = value) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { estado -> estado.copy(passwordVisible = !estado.passwordVisible) }
    }

    fun onCrearCuenta() {
        // Aquí irá la lógica de registro
    }
}