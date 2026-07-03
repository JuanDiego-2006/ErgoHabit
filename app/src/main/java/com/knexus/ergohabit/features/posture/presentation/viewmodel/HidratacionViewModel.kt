package com.knexus.ergohabit.features.posture.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knexus.ergohabit.features.posture.domain.usecase.*
import com.knexus.ergohabit.features.posture.presentation.receiver.HidratacionReceiver
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val configurarMetaPesoAguaUseCase: ConfigurarMetaPesoAguaUseCase,
    private val toggleNotificacionesAguaUseCase: ToggleNotificacionesAguaUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(HidratacionUiState())
    val uiState: StateFlow<HidratacionUiState> = _uiState.asStateFlow()

    init {
        cargarDashboard()
        // Iniciamos el ciclo de recordatorios cada hora si no está programado
        HidratacionReceiver.programarSiguienteAlarma(context)
    }

    fun cargarDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getDashboardAguaUseCase().collect { result ->
                result.onSuccess { dashboard ->
                    _uiState.update { it.copy(
                        mlActuales = dashboard.consumidoHoyMl,
                        mlObjetivo = dashboard.metaDiariaMl,
                        vasosObjetivo = (dashboard.metaDiariaMl / 245.0).toInt(),
                        pesoActual = dashboard.pesoActual.toInt(),
                        estaturaActual = dashboard.estaturaActual,
                        pesoInput = dashboard.pesoActual.toInt().toString(),
                        estaturaInput = (dashboard.estaturaActual * 100).toInt().toString(),
                        fraseMotivacional = dashboard.fraseMotivacional,
                        tipsHidratacion = dashboard.tipsHidratacion,
                        notificacionesActivas = dashboard.notificacionesActivas,
                        isLoading = false
                    ) }
                }.onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
            }
        }
    }

    fun agregarAgua(ml: Int) {
        viewModelScope.launch {
            // Sincronización silenciosa (Room se encarga de actualizar la UI instantáneamente)
            registrarTomaAguaUseCase(ml).onFailure { error ->
                _uiState.update { it.copy(error = "Fallo de conexión: Tu registro se sincronizará luego") }
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
        if (newInput.length > 3) return
        val filtered = newInput.filter { it.isDigit() }
        _uiState.update { it.copy(
            estaturaInput = filtered,
            estaturaActual = (filtered.toIntOrNull() ?: 0) / 100.0
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

    fun toggleNotificaciones() {
        val nuevoEstado = !_uiState.value.notificacionesActivas
        _uiState.update { it.copy(notificacionesActivas = nuevoEstado) }
        viewModelScope.launch {
            toggleNotificacionesAguaUseCase(nuevoEstado)
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}
