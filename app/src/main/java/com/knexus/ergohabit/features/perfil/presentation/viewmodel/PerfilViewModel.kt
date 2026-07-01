package com.knexus.ergohabit.features.perfil.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knexus.ergohabit.core.session.SessionManager
import com.knexus.ergohabit.features.perfil.domain.entities.UsuarioPerfil
import com.knexus.ergohabit.features.perfil.domain.usecases.GetPerfilUseCase
import com.knexus.ergohabit.features.perfil.domain.usecases.UpdateFotoPerfilUseCase
import com.knexus.ergohabit.features.perfil.domain.usecases.UpdatePerfilUseCase
import com.knexus.ergohabit.features.perfil.domain.usecases.ClearLocalProfileUseCase
import com.knexus.ergohabit.features.perfil.domain.usecases.EliminarPerfilUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PerfilUiState(
    val usuario: UsuarioPerfil? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val isEditing: Boolean = false,
    val editNombre: String = "",
    val editPrimerApellido: String = "",
    val editSegundoApellido: String = ""
)

@HiltViewModel
class PerfilViewModel @Inject constructor(
    private val getPerfilUseCase: GetPerfilUseCase,
    private val updatePerfilUseCase: UpdatePerfilUseCase,
    private val updateFotoPerfilUseCase: UpdateFotoPerfilUseCase,
    private val clearLocalProfileUseCase: ClearLocalProfileUseCase,
    private val eliminarPerfilUseCase: EliminarPerfilUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    fun eliminarCuenta(onDeleteSuccess: () -> Unit) {
        val idUsuario = sessionManager.fetchUserId()
        if (idUsuario == -1) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            eliminarPerfilUseCase(idUsuario).onSuccess {
                clearLocalProfileUseCase()
                sessionManager.clearSession()
                onDeleteSuccess()
            }.onFailure { error ->
                _uiState.update { it.copy(error = error.message, isLoading = false) }
            }
        }
    }

    init {
        cargarPerfil()
    }

    fun cargarPerfil() {
        val idUsuario = sessionManager.fetchUserId()
        if (idUsuario == -1) {
            _uiState.update { it.copy(error = "No se pudo identificar al usuario") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getPerfilUseCase(idUsuario).collect { result ->
                result.onSuccess { perfil ->
                    _uiState.update { it.copy(
                        usuario = perfil,
                        isLoading = false,
                        editNombre = perfil.nombre,
                        editPrimerApellido = perfil.primerApellido,
                        editSegundoApellido = perfil.segundoApellido
                    ) }
                }.onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
            }
        }
    }

    fun actualizarFoto(uri: String) {
        val idUsuario = sessionManager.fetchUserId()
        if (idUsuario == -1) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            updateFotoPerfilUseCase(idUsuario, uri).onSuccess { nuevaUrl ->
                _uiState.update { it.copy(
                    usuario = it.usuario?.copy(fotoUrl = nuevaUrl),
                    isLoading = false,
                    successMessage = "Foto actualizada con éxito"
                ) }
            }.onFailure { error ->
                _uiState.update { it.copy(error = error.message, isLoading = false) }
            }
        }
    }

    fun toggleEdit(enabled: Boolean) {
        _uiState.update { it.copy(
            isEditing = enabled,
            editNombre = it.usuario?.nombre ?: "",
            editPrimerApellido = it.usuario?.primerApellido ?: "",
            editSegundoApellido = it.usuario?.segundoApellido ?: ""
        ) }
    }

    fun onNombreChange(newValue: String) {
        _uiState.update { it.copy(editNombre = newValue) }
    }

    fun onPrimerApellidoChange(newValue: String) {
        _uiState.update { it.copy(editPrimerApellido = newValue) }
    }

    fun onSegundoApellidoChange(newValue: String) {
        _uiState.update { it.copy(editSegundoApellido = newValue) }
    }

    fun guardarCambios() {
        val idUsuario = sessionManager.fetchUserId()
        val currentUsuario = _uiState.value.usuario ?: return

        val updatedUsuario = currentUsuario.copy(
            nombre = _uiState.value.editNombre,
            primerApellido = _uiState.value.editPrimerApellido,
            segundoApellido = _uiState.value.editSegundoApellido
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            updatePerfilUseCase(idUsuario, updatedUsuario).onSuccess { msg ->
                _uiState.update { it.copy(
                    usuario = updatedUsuario,
                    isEditing = false,
                    isLoading = false,
                    successMessage = msg
                ) }
            }.onFailure { error ->
                _uiState.update { it.copy(error = error.message, isLoading = false) }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }

    fun cerrarSesion(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            clearLocalProfileUseCase()
            sessionManager.clearSession()
            onLogoutSuccess()
        }
    }
}
