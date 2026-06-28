package com.knexus.ergohabit.features.progreso.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knexus.ergohabit.core.session.SessionManager
import com.knexus.ergohabit.features.progreso.domain.usecases.GetDetalleHabitoUseCase
import com.knexus.ergohabit.features.progreso.domain.usecases.GetHabitosProgresoUseCase
import com.knexus.ergohabit.features.progreso.domain.usecases.GetTendenciaGeneralUseCase
import com.knexus.ergohabit.features.progreso.presentation.screens.ProgresoUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgresoViewModel @Inject constructor(
    private val getHabitosProgresoUseCase: GetHabitosProgresoUseCase,
    private val getTendenciaGeneralUseCase: GetTendenciaGeneralUseCase,
    private val getDetalleHabitoUseCase: GetDetalleHabitoUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgresoUiState())
    val uiState: StateFlow<ProgresoUiState> = _uiState.asStateFlow()

    init {
        sessionManager.fetchUserId()?.let { loadProgreso(it) }
    }

    fun seleccionarHabito(idHabito: Int) {
        val userId = sessionManager.fetchUserId() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(idHabitoSeleccionado = idHabito, isLoading = true) }
            getDetalleHabitoUseCase(userId, idHabito).collect { result ->
                result.onSuccess { detalle ->
                    _uiState.update { it.copy(detalleHabito = detalle, isLoading = false) }
                }.onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
            }
        }
    }

    fun loadProgreso(idUsuario: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getHabitosProgresoUseCase(idUsuario).collect { result ->
                result.onSuccess { habitos ->
                    _uiState.update { it.copy(habitos = habitos) }
                }.onFailure { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
            }

            getTendenciaGeneralUseCase(idUsuario).collect { result ->
                result.onSuccess { (porcentaje, tendencia) ->
                    _uiState.update {
                        it.copy(
                            tendencia = tendencia,
                            porcentajeTendencia = porcentaje,
                            isLoading = false
                        )
                    }
                }.onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
            }
        }
    }

    fun refrescarProgreso() {
        val userId = sessionManager.fetchUserId() ?: return
        val habitoSeleccionado = _uiState.value.idHabitoSeleccionado
        loadProgreso(userId)
        if (habitoSeleccionado != null) {
            seleccionarHabito(habitoSeleccionado)
        }
    }
}
