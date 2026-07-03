package com.knexus.ergohabit.features.posture.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knexus.ergohabit.features.posture.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    // --- CAMBIO REALIZADO: Inyección del Caso de Uso de Registro ---
    private val registerUseCase: RegisterUseCase
    // ---------------------------------------------------------------
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNombreChange(value: String) {
        _uiState.update { estado -> estado.copy(nombre = value, errorMessage = null) }
    }

    fun onPrimerApellidoChange(value: String) {
        _uiState.update { estado -> estado.copy(primerApellido = value, errorMessage = null) }
    }

    fun onSegundoApellidoChange(value: String) {
        _uiState.update { estado -> estado.copy(segundoApellido = value, errorMessage = null) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { estado -> estado.copy(email = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { estado -> estado.copy(password = value, errorMessage = null) }
    }

    fun onPrivacidadChange(value: Boolean) {
        _uiState.update { estado -> estado.copy(aceptoPrivacidad = value) }
    }

    fun onTerminosChange(value: Boolean) {
        _uiState.update { estado -> estado.copy(aceptoTerminos = value) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { estado -> estado.copy(passwordVisible = !estado.passwordVisible) }
    }

    fun onCrearCuenta() {
        // --- CAMBIO REALIZADO: Implementación de la lógica de Registro con la API ---
        val estado = _uiState.value
        
        if (estado.nombre.isBlank() || estado.primerApellido.isBlank() || 
            estado.email.isBlank() || estado.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Por favor, completa los campos obligatorios (*)") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            val segundoApellido = if (estado.segundoApellido.isBlank()) null else estado.segundoApellido

            registerUseCase(
                nombre = estado.nombre,
                primerApellido = estado.primerApellido,
                segundoApellido = segundoApellido,
                email = estado.email,
                contrasena = estado.password
            ).onSuccess {
                _uiState.update { it.copy(
                    isLoading = false, 
                    isRegisterSuccess = true,
                    successMessage = "¡Cuenta creada con éxito! Redirigiendo..."
                ) }
            }.onFailure { error ->
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = error.message ?: "Error al crear la cuenta" 
                    ) 
                }
            }
        }
        // ------------------------------------------------------------------------
    }

    fun resetNavigation() {
        _uiState.update { it.copy(isRegisterSuccess = false) }
    }
}