package com.knexus.ergohabit.features.progreso.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knexus.ergohabit.features.progreso.domain.usecases.GetHabitosProgresoUseCase
import com.knexus.ergohabit.features.progreso.domain.usecases.GetFraseAleatoriaUseCase
import com.knexus.ergohabit.features.progreso.domain.usecases.GetDetalleHabitoUseCase
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
    private val getFraseAleatoriaUseCase: GetFraseAleatoriaUseCase,
    private val getDetalleHabitoUseCase: GetDetalleHabitoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgresoUiState())
    val uiState: StateFlow<ProgresoUiState> = _uiState.asStateFlow()

    init {
        loadProgreso(1)
    }

    fun seleccionarHabito(idHabito: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(idHabitoSeleccionado = idHabito, isLoading = true) }
            getDetalleHabitoUseCase(1, idHabito).collect { result ->
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
            _uiState.update { it.copy(isLoading = true) }
            

            getHabitosProgresoUseCase(idUsuario).collect { result ->
                result.onSuccess { habitos ->
                    _uiState.update { it.copy(habitos = habitos) }
                }.onFailure { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
            }


            getFraseAleatoriaUseCase().collect { result ->
                result.onSuccess { frase ->
                    _uiState.update { it.copy(frase = frase, isLoading = false) }
                }.onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
            }
        }
    }
}
