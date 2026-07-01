package com.knexus.ergohabit.features.posture.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knexus.ergohabit.features.posture.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HidratacionViewModel @Inject constructor(
    private val getDashboardAguaUseCase: GetDashboardAguaUseCase,
    private val registrarTomaAguaUseCase: RegistrarTomaAguaUseCase,
    private val configurarMetaManualAguaUseCase: ConfigurarMetaManualAguaUseCase,
    private val configurarMetaPesoAguaUseCase: ConfigurarMetaPesoAguaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HidratacionUiState())
    val uiState: StateFlow<HidratacionUiState> = _uiState.asStateFlow()

    init {
        cargarDashboard()
    }

    fun cargarDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getDashboardAguaUseCase().onSuccess { dashboard ->
                _uiState.update { it.copy(
                    mlActuales = dashboard.consumidoHoyMl,
                    mlObjetivo = dashboard.metaDiariaMl,
                    vasosObjetivo = (dashboard.metaDiariaMl / 245.0).toInt(),
                    pesoActual = dashboard.pesoActual.toInt(),
                    estaturaActual = dashboard.estaturaActual,
                    pesoInput = dashboard.pesoActual.toInt().toString(),
                    estaturaInput = String.format(Locale.US, "%.2f", dashboard.estaturaActual),
                    fraseMotivacional = dashboard.fraseMotivacional,
                    tipsHidratacion = dashboard.tipsHidratacion,
                    isLoading = false
                ) }
            }.onFailure { error ->
                _uiState.update { it.copy(error = error.message, isLoading = false) }
            }
        }
    }

    fun agregarAgua(ml: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            registrarTomaAguaUseCase(ml).onSuccess {
                cargarDashboard()
            }.onFailure { error ->
                _uiState.update { it.copy(error = error.message, isLoading = false) }
            }
        }
    }

    fun mostrarDialogoAjustarMeta(mostrar: Boolean) {
        _uiState.update { it.copy(mostrarDialogoMeta = mostrar) }
    }

    fun seleccionarMetodoCalculo(metodo: Int) {
        _uiState.update { it.copy(metodoCalculoSeleccionado = metodo) }
    }

    fun continuarAjusteMeta() {
        val metodo = _uiState.value.metodoCalculoSeleccionado
        if (metodo == 0) {
            _uiState.update { it.copy(mostrarDialogoMeta = false, mostrarDialogoPesoEstatura = true) }
        } else {
            // Ir a meta manual
            _uiState.update { it.copy(
                mostrarDialogoMeta = false,
                mostrarDialogoMetaManual = true,
                metaManualTemporal = it.mlObjetivo,
                metaManualInput = it.mlObjetivo.toString()
            ) }
        }
    }

    fun mostrarDialogoPesoEstatura(mostrar: Boolean) {
        _uiState.update { it.copy(mostrarDialogoPesoEstatura = mostrar) }
    }

    fun mostrarDialogoMetaManual(mostrar: Boolean) {
        _uiState.update { it.copy(mostrarDialogoMetaManual = mostrar) }
    }

    fun mostrarDialogoCustomAmount(mostrar: Boolean) {
        _uiState.update { it.copy(mostrarDialogoCustomAmount = mostrar, customAmountTemporal = 250) }
    }

    fun setEditandoPeso(esPeso: Boolean) {
        _uiState.update { it.copy(editandoPeso = esPeso) }
    }

    fun onPesoInputChange(newInput: String) {
        if (newInput.length > 3) return
        val filtered = newInput.filter { it.isDigit() }
        _uiState.update { it.copy(
            pesoInput = filtered,
            pesoActual = filtered.toIntOrNull() ?: it.pesoActual
        ) }
    }

    fun onEstaturaInputChange(newInput: String) {
        if (newInput.length > 4) return
        // Permitir dígitos y punto decimal
        val filtered = newInput.filter { it.isDigit() || it == '.' }
        _uiState.update { it.copy(
            estaturaInput = filtered,
            estaturaActual = filtered.toDoubleOrNull() ?: it.estaturaActual
        ) }
    }

    fun onMetaManualInputChange(newInput: String) {
        if (newInput.length > 5) return
        // Permitir números y punto decimal
        val filtered = newInput.filter { it.isDigit() || it == '.' }

        val mlValue = if (filtered.contains('.')) {
            (filtered.toDoubleOrNull() ?: 0.0) * 1000
        } else {
            filtered.toDoubleOrNull() ?: 0.0
        }

        _uiState.update { it.copy(
            metaManualInput = filtered,
            metaManualTemporal = if (mlValue >= 100) mlValue.toInt() else it.metaManualTemporal
        ) }
    }

    fun ajustarMetaManual(incremento: Boolean) {
        _uiState.update { estado ->
            val paso = 100
            val nuevaMetaMl = if (incremento) estado.metaManualTemporal + paso else (estado.metaManualTemporal - paso).coerceAtLeast(100)

            val isLitros = nuevaMetaMl > 900
            val nuevoInput = if (isLitros) {
                String.format(Locale.US, "%.1f", nuevaMetaMl / 1000f)
            } else {
                nuevaMetaMl.toString()
            }

            estado.copy(
                metaManualTemporal = nuevaMetaMl,
                metaManualInput = nuevoInput
            )
        }
    }

    fun ajustarCustomAmount(incremento: Boolean) {
        _uiState.update { estado ->
            val paso = 50
            val nuevoValor = if (incremento) estado.customAmountTemporal + paso else (estado.customAmountTemporal - paso).coerceAtLeast(50)
            estado.copy(customAmountTemporal = nuevoValor)
        }
    }

    fun setMetaManual(ml: Int) {
        val isLitros = ml > 900
        val nuevoInput = if (isLitros) {
            String.format(Locale.US, "%.1f", ml / 1000f)
        } else {
            ml.toString()
        }
        _uiState.update { it.copy(
            metaManualTemporal = ml,
            metaManualInput = nuevoInput
        ) }
    }

    fun guardarMetaCalculada() {
        viewModelScope.launch {
            val peso = _uiState.value.pesoActual.toDouble()
            val estatura = _uiState.value.estaturaActual

            _uiState.update { it.copy(isLoading = true) }
            configurarMetaPesoAguaUseCase(peso, estatura).onSuccess {
                _uiState.update { it.copy(mostrarDialogoPesoEstatura = false, successMessage = "¡Meta actualizada!") }
                cargarDashboard()
            }.onFailure { error ->
                _uiState.update { it.copy(error = error.message, isLoading = false) }
            }
        }
    }

    fun guardarMetaManual() {
        viewModelScope.launch {
            val meta = _uiState.value.metaManualTemporal
            _uiState.update { it.copy(isLoading = true) }
            configurarMetaManualAguaUseCase(meta).onSuccess {
                _uiState.update { it.copy(mostrarDialogoMetaManual = false, successMessage = "¡Meta actualizada!") }
                cargarDashboard()
            }.onFailure { error ->
                _uiState.update { it.copy(error = error.message, isLoading = false) }
            }
        }
    }

    fun confirmarCustomAmount() {
        agregarAgua(_uiState.value.customAmountTemporal)
        _uiState.update { it.copy(mostrarDialogoCustomAmount = false) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}
