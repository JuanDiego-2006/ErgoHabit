package com.knexus.ergohabit.features.perfil.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knexus.ergohabit.core.session.SessionManager
import com.knexus.ergohabit.features.perfil.domain.entities.UsuarioPerfil
import com.knexus.ergohabit.features.perfil.domain.repositories.PerfilRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PerfilUiState(
    val usuario: UsuarioPerfil? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEditing: Boolean = false,
    val editNombre: String = "",
    val editPrimerApellido: String = "",
    val editSegundoApellido: String = ""
)

@HiltViewModel
class PerfilViewModel @Inject constructor(
    private val repository: PerfilRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    init {
        cargarPerfil()
    }

    private fun cargarPerfil() {
        val userId = sessionManager.fetchUserId() ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.getPerfil(userId).collect { result ->
                result.onSuccess { perfil ->
                    _uiState.value = _uiState.value.copy(
                        usuario = perfil,
                        isLoading = false,
                        editNombre = perfil.nombre,
                        editPrimerApellido = perfil.primerApellido,
                        editSegundoApellido = perfil.segundoApellido
                    )
                }.onFailure { error ->
                    _uiState.value = _uiState.value.copy(error = error.message, isLoading = false)
                }
            }
        }
    }

    fun toggleEdit(enabled: Boolean) {
        if (enabled) {
            _uiState.value = _uiState.value.copy(
                isEditing = true,
                editNombre = _uiState.value.usuario?.nombre ?: "",
                editPrimerApellido = _uiState.value.usuario?.primerApellido ?: "",
                editSegundoApellido = _uiState.value.usuario?.segundoApellido ?: ""
            )
        } else {
            _uiState.value = _uiState.value.copy(isEditing = false)
        }
    }

    fun onNombreChange(newValue: String) {
        _uiState.value = _uiState.value.copy(editNombre = newValue)
    }

    fun onPrimerApellidoChange(newValue: String) {
        _uiState.value = _uiState.value.copy(editPrimerApellido = newValue)
    }

    fun onSegundoApellidoChange(newValue: String) {
        _uiState.value = _uiState.value.copy(editSegundoApellido = newValue)
    }

    fun guardarCambios() {
        viewModelScope.launch {
            val currentUsuario = _uiState.value.usuario ?: return@launch
            val updatedUsuario = currentUsuario.copy(
                nombre = _uiState.value.editNombre,
                primerApellido = _uiState.value.editPrimerApellido,
                segundoApellido = _uiState.value.editSegundoApellido
            )
            
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.updatePerfil(updatedUsuario).onSuccess {
                _uiState.value = _uiState.value.copy(
                    usuario = updatedUsuario,
                    isEditing = false,
                    isLoading = false
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(error = error.message, isLoading = false)
            }
        }
    }

    fun actualizarFoto(uri: String) {
        val userId = sessionManager.fetchUserId() ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repository.updateFotoPerfil(userId, uri).onSuccess { nuevaUrl ->
                _uiState.value = _uiState.value.copy(
                    usuario = _uiState.value.usuario?.copy(fotoUrl = nuevaUrl),
                    isLoading = false
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    error = error.message ?: "No se pudo subir la foto de perfil.",
                    isLoading = false
                )
            }
        }
    }

    fun limpiarError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun cerrarSesion(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            // --- CAMBIO REALIZADO: Limpiar token y cerrar sesión ---
            sessionManager.clearSession()
            // -------------------------------------------------------
            onLogoutSuccess()
        }
    }
}
