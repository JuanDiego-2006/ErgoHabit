package com.knexus.ergohabit.features.posture.presentation.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knexus.ergohabit.core.hardware.domain.SensorEjercicio
import com.knexus.ergohabit.features.posture.data.datasource.api.HabitosApi
import com.knexus.ergohabit.features.posture.data.models.RegistrarKmRequest
import com.knexus.ergohabit.features.posture.domain.GestorEjercicio
import com.knexus.ergohabit.features.posture.presentation.services.StepCounterService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActividadViewModel @Inject constructor(
    private val api: HabitosApi,
    private val sensorEjercicio: SensorEjercicio,
    private val gestorEjercicio: GestorEjercicio,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActividadUiState())
    val uiState: StateFlow<ActividadUiState> = _uiState.asStateFlow()

    private var sensorJob: Job? = null

    init {
        _uiState.update {
            it.copy(sensorDisponible = sensorEjercicio.estaDisponible())
        }
        cargarDashboard()
        observarGestor()
    }

    private fun observarGestor() {
        viewModelScope.launch {
            gestorEjercicio.datosPasos.collect { datos ->
                _uiState.update {
                    it.copy(
                        pasosSesion = datos.pasos,
                        kmSesion = datos.km.toFloat()
                    )
                }
            }
        }
        viewModelScope.launch {
            gestorEjercicio.estaCorriendo.collect { activo ->
                _uiState.update { it.copy(sensorActivo = activo) }
            }
        }
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

        val intent = Intent(context, StepCounterService::class.java)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun detenerSensor() {
        val intent = Intent(context, StepCounterService::class.java)
        context.stopService(intent)
    }

    fun sincronizarSesion() {
        val km = _uiState.value.kmSesion.toDouble()
        if (km < 0.01) {
            _uiState.update { it.copy(errorSensor = "Camina un poco más antes de guardar.") }
            return
        }
        val estabaActivo = _uiState.value.sensorActivo
        registrarKm(km) {
            if (estabaActivo) {
                detenerSensor()
                gestorEjercicio.reiniciar()
                iniciarSensor()
            } else {
                gestorEjercicio.reiniciar()
            }
            _uiState.update { it.copy(pasosSesion = 0, kmSesion = 0f, errorSensor = null) }
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