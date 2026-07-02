package com.knexus.ergohabit.features.posture.presentation.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knexus.ergohabit.core.hardware.domain.SensorEjercicio
import com.knexus.ergohabit.features.posture.domain.GestorEjercicio
import com.knexus.ergohabit.features.posture.domain.repository.EjercicioRepository
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
    private val repository: EjercicioRepository,
    private val sensorEjercicio: SensorEjercicio,
    private val gestorEjercicio: GestorEjercicio,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActividadUiState())
    val uiState: StateFlow<ActividadUiState> = _uiState.asStateFlow()

    private var dashboardJob: Job? = null

    init {
        _uiState.update {
            it.copy(sensorDisponible = sensorEjercicio.estaDisponible())
        }
        observarDashboard()
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

    private fun observarDashboard() {
        dashboardJob?.cancel()
        dashboardJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getDashboardEjercicio().collect { resultado ->
                resultado.onSuccess { respuesta ->
                    val kmActuales = parseKmNumerico(respuesta.kmRecorridosText) ?: 0f
                    val kmObjetivo = parseKmNumerico(respuesta.metaKmText) ?: 0f

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
                }.onFailure {
                    _uiState.update { it.copy(isLoading = false, isRegistrando = false) }
                }
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
        
        // Validación local con mensaje descriptivo
        if (km < 0.01) {
            _uiState.update { 
                it.copy(errorSensor = "¡Sigue así! Necesitas acumular al menos 10 metros para guardar tu progreso. 🚶") 
            }
            return
        }

        val estabaActivo = _uiState.value.sensorActivo
        registrarKm(km) {
            // El reinicio del Gestor solo ocurre si el registro fue exitoso
            gestorEjercicio.reiniciar()
            
            if (estabaActivo) {
                detenerSensor()
                iniciarSensor()
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

    private fun registrarKm(km: Double, onExito: () -> Unit = {}) {
        if (_uiState.value.isRegistrando) return
        viewModelScope.launch {
            _uiState.update { it.copy(isRegistrando = true, errorSensor = null) }
            repository.registrarKilometros(km).onSuccess {
                onExito()
                _uiState.update { it.copy(isRegistrando = false) }
            }.onFailure { error ->
                val mensajeError = error.message ?: "No se pudo guardar el progreso. Revisa tu conexión."
                _uiState.update {
                    it.copy(
                        isRegistrando = false,
                        errorSensor = if (mensajeError.contains("400")) 
                            "La cantidad es muy pequeña para el servidor. Camina un poco más." 
                            else mensajeError
                    )
                }
            }
        }
    }

    private fun parseKmNumerico(texto: String): Float? {
        // Limpiamos cualquier carácter no numérico excepto el punto y la coma
        val limpio = texto
            .replace("de ", "", ignoreCase = true)
            .replace(" km", "", ignoreCase = true)
            .replace(",", ".")
            .filter { it.isDigit() || it == '.' }
            .trim()
        
        return limpio.toFloatOrNull()
    }
}
