package com.knexus.ergohabit.features.posture.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knexus.ergohabit.core.hardware.domain.SensorEjercicio
import com.knexus.ergohabit.features.posture.data.datasource.api.HabitosApi
import com.knexus.ergohabit.features.posture.data.models.RegistrarKmRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActividadViewModel @Inject constructor(
    private val api: HabitosApi,
    private val sensorEjercicio: SensorEjercicio
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActividadUiState())
    val uiState: StateFlow<ActividadUiState> = _uiState.asStateFlow()

    private var sensorJob: Job? = null

    init {
        _uiState.update {
            it.copy(sensorDisponible = sensorEjercicio.estaDisponible())
        }
        cargarDashboard()
    }

    private fun cargarDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val respuesta = api.obtenerDashboardEjercicio()
                val kmActuales = parseKmNumerico(respuesta.kmRecorridosText) ?: 0f
                val kmObjetivo = parseKmNumerico(respuesta.metaKmText) ?: 8f

                _uiState.update {
                    it.copy(
                        kmActuales = kmActuales,
                        kmObjetivo = kmObjetivo,
                        calorias = respuesta.caloriasQuemadas,
                        rachasDias = respuesta.rachaDias,
                        mensajeFaltante = respuesta.mensajeFaltanteText,
                        sugerencia = respuesta.sugerenciaCaminataText,
                        fraseMotivacional = respuesta.fraseMotivacional,
                        porcentajeBackend = respuesta.porcentajeCumplimiento,
                        isLoading = false,
                        isRegistrando = false
                    )
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(isLoading = false, isRegistrando = false) }
            }
        }
    }

    fun iniciarSensor() {
        if (_uiState.value.sensorActivo) return

        if (!sensorEjercicio.estaDisponible()) {
            _uiState.update {
                it.copy(errorSensor = "Tu dispositivo no tiene sensor de pasos.")
            }
            return
        }

        sensorJob?.cancel()
        sensorJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    sensorActivo = true,
                    pasosSesion = 0,
                    kmSesion = 0f,
                    errorSensor = null
                )
            }

            sensorEjercicio.iniciarMonitoreo()
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            sensorActivo = false,
                            errorSensor = error.message ?: "No se pudo iniciar el sensor."
                        )
                    }
                }
                .collect { datos ->
                    _uiState.update {
                        it.copy(
                            pasosSesion = datos.pasos,
                            kmSesion = datos.km.toFloat()
                        )
                    }
                }
        }
    }

    fun detenerSensor() {
        sensorJob?.cancel()
        sensorJob = null
        sensorEjercicio.detenerMonitoreo()
        _uiState.update { it.copy(sensorActivo = false) }
    }

    fun sincronizarSesion() {
        val km = _uiState.value.kmSesion.toDouble()
        if (km < 0.01) {
            _uiState.update { it.copy(errorSensor = "Camina un poco más antes de guardar.") }
            return
        }
        registrarKm(km) {
            _uiState.update { it.copy(pasosSesion = 0, kmSesion = 0f, errorSensor = null) }
            if (_uiState.value.sensorActivo) {
                reiniciarSensor()
            }
        }
    }

    fun onPermisoDenegado() {
        _uiState.update {
            it.copy(errorSensor = "Se necesita permiso de actividad física para contar pasos.")
        }
    }

    fun limpiarErrorSensor() {
        _uiState.update { it.copy(errorSensor = null) }
    }

    private fun reiniciarSensor() {
        detenerSensor()
        iniciarSensor()
    }

    private fun registrarKm(km: Double, onExito: () -> Unit = {}) {
        if (_uiState.value.isRegistrando) return
        viewModelScope.launch {
            _uiState.update { it.copy(isRegistrando = true) }
            try {
                api.registrarKilometros(RegistrarKmRequest(km = km))
                onExito()
                cargarDashboard()
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        isRegistrando = false,
                        errorSensor = "No se pudo guardar el progreso. Revisa tu conexión."
                    )
                }
            }
        }
    }

    override fun onCleared() {
        detenerSensor()
        super.onCleared()
    }

    private fun parseKmNumerico(texto: String): Float? {
        return texto
            .substringAfter("de ", texto)
            .replace(" km", "", ignoreCase = true)
            .replace(",", ".")
            .trim()
            .toFloatOrNull()
    }
}
