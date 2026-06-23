package com.knexus.ergohabit.features.posture.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knexus.ergohabit.core.session.SessionManager
import com.knexus.ergohabit.features.posture.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    // --- CAMBIO REALIZADO: Inyección del Caso de Uso de Login y SessionManager ---
    private val loginUseCase: LoginUseCase,
    private val sessionManager: SessionManager
    // ----------------------------------------------------------------------------
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, errorMessage = null) }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, errorMessage = null) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun onLoginClick() {
        // --- CAMBIO REALIZADO: Implementación de la lógica de Login con la API ---
        val email = _uiState.value.email
        val password = _uiState.value.password

        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Por favor, completa todos los campos") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            loginUseCase(email, password)
                .onSuccess { sesion ->
                    // --- CAMBIO REALIZADO: Guardar el token de forma persistente ---
                    sessionManager.saveAuthToken(sesion.token)
                    // ---------------------------------------------------------------
                    _uiState.update { it.copy(isLoading = false, isLoginSuccess = true) }
                }
                .onFailure { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = error.message ?: "Error al iniciar sesión"
                        ) 
                    }
                }
        }
        // ------------------------------------------------------------------------
    }
    
    fun resetNavigation() {
        _uiState.update { it.copy(isLoginSuccess = false) }
    }
}