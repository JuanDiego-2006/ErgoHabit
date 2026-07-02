package com.knexus.ergohabit.features.posture.domain

import com.knexus.ergohabit.core.hardware.domain.model.ResultadoPosturaCamara
import com.knexus.ergohabit.core.hardware.domain.GestorSonido
import com.knexus.ergohabit.features.posture.domain.entities.EntidadPostura
import com.knexus.ergohabit.features.posture.domain.repository.PostureRepository
import com.knexus.ergohabit.features.posture.domain.usecase.PosturaUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

data class EstadoMonitoreoPostura(
    val activo: Boolean = false,
    val anguloPitch: Double = 0.0,
    val anguloRoll: Double = 0.0,
    val esCorrecta: Boolean = true,
    val mensaje: String = "Inactivo",
    val gradosDisplay: Int = 0,
    val conteoAlertas: Int = 0,
    val mensajeCamara: String = "",
    val camaraActiva: Boolean = false,
    val alertaPorCamara: Boolean = false
)

@Singleton
class GestorMonitoreoPostura @Inject constructor(
    private val posturaUseCase: PosturaUseCase,
    private val postureRepository: PostureRepository,
    private val gestorSonido: GestorSonido
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _estado = MutableStateFlow(EstadoMonitoreoPostura())
    val estado: StateFlow<EstadoMonitoreoPostura> = _estado.asStateFlow()

    private var monitoreoJob: Job? = null
    private var syncJob: Job? = null
    private var alertaActiva = false
    private var ultimoResultadoCamara: ResultadoPosturaCamara? = null
    private var ultimoSensorCorrecto = true
    private var ultimoPitch = 0.0
    private var ultimoRoll = 0.0
    private var lecturasMalasCamara = 0
    private var ultimaAlertaSonora = 0L

    fun iniciar() {
        if (_estado.value.activo) return
        _estado.update { it.copy(activo = true, conteoAlertas = 0, mensaje = "Monitoreando...") }
        iniciarRecoleccion()
        iniciarSincronizacionPeriodica()
    }

    fun detener() {
        monitoreoJob?.cancel()
        monitoreoJob = null
        // Sincronización final antes de detener todo
        scope.launch { postureRepository.sincronizarConServidor() }
        syncJob?.cancel()
        syncJob = null
        alertaActiva = false
        ultimoResultadoCamara = null
        lecturasMalasCamara = 0
        gestorSonido.detenerSonido()
        _estado.update {
            it.copy(
                activo = false,
                esCorrecta = true,
                mensaje = "Inactivo",
                gradosDisplay = 0,
                mensajeCamara = "",
                camaraActiva = false,
                alertaPorCamara = false
            )
        }
    }

    fun pausar() {
        monitoreoJob?.cancel()
        monitoreoJob = null
        // Al pausar (salir de la app), enviamos lo acumulado de inmediato para que no se pierda
        scope.launch { postureRepository.sincronizarConServidor() }
        alertaActiva = false
        gestorSonido.detenerSonido()
    }

    fun reanudar() {
        if (!_estado.value.activo || monitoreoJob?.isActive == true) return
        iniciarRecoleccion()
        iniciarSincronizacionPeriodica()
    }

    private fun iniciarSincronizacionPeriodica() {
        if (syncJob?.isActive == true) return
        
        syncJob = scope.launch {
            while (true) {
                // Sincronización cada 2 minutos para pruebas
                kotlinx.coroutines.delay(20 * 60 * 1000)
                postureRepository.sincronizarConServidor()
            }
        }
    }

    fun actualizarCamara(resultado: ResultadoPosturaCamara) {
        ultimoResultadoCamara = resultado

        if (resultado.personaDetectada && !resultado.esCorrecta) {
            lecturasMalasCamara++
        } else {
            lecturasMalasCamara = 0
        }

        _estado.update {
            it.copy(
                camaraActiva = resultado.personaDetectada,
                mensajeCamara = resultado.motivo,
                alertaPorCamara = lecturasMalasCamara >= 2
            )
        }
        evaluarEstadoCombinado()
    }

    fun marcarCamaraInactiva() {
        ultimoResultadoCamara = null
        lecturasMalasCamara = 0
        _estado.update {
            it.copy(
                camaraActiva = false,
                mensajeCamara = "Cámara inactiva",
                alertaPorCamara = false
            )
        }
        evaluarEstadoCombinado()
    }

    private fun iniciarRecoleccion() {
        monitoreoJob?.cancel()
        monitoreoJob = scope.launch {
            posturaUseCase().collect { entidad ->
                ultimoSensorCorrecto = entidad.esCorrecta
                ultimoPitch = entidad.anguloPitch
                ultimoRoll = entidad.anguloRoll
                val gradosMalos = abs(90.0 - abs(entidad.anguloPitch)).toInt().coerceIn(0, 60)
                _estado.update {
                    it.copy(
                        anguloPitch = entidad.anguloPitch,
                        anguloRoll = entidad.anguloRoll,
                        gradosDisplay = gradosMalos
                    )
                }
                evaluarEstadoCombinado()
            }
        }
    }

    private fun evaluarEstadoCombinado() {
        val camara = ultimoResultadoCamara
        val alertaCamara = lecturasMalasCamara >= 2
        val esIncorrecta = !ultimoSensorCorrecto || alertaCamara

        val mensaje = when {
            alertaCamara -> camara?.motivo ?: "Postura corporal incorrecta"
            !ultimoSensorCorrecto -> when {
                abs(90.0 - abs(ultimoPitch)) <= 40.0 -> "¡Eleva tu teléfono!"
                else -> "¡Corrige tu postura!"
            }
            else -> "Postura Correcta"
        }

        if (esIncorrecta) {
            if (!alertaActiva) {
                alertaActiva = true
                _estado.update { it.copy(conteoAlertas = it.conteoAlertas + 1) }
                scope.launch(Dispatchers.IO) {
                    postureRepository.guardarReporteLocal(
                        EntidadPostura(
                            anguloPitch = ultimoPitch,
                            anguloRoll = ultimoRoll,
                            esCorrecta = false,
                            mensaje = mensaje
                        )
                    )
                }
            }
            
            // Intervalo equilibrado (4 segundos) para que no sea acosador
            val ahora = System.currentTimeMillis()
            if (ahora - ultimaAlertaSonora > 4000) {
                android.util.Log.d("GestorPostura", "Ejecutando alerta (intervalo 4s)")
                gestorSonido.sonarAlerta()
                ultimaAlertaSonora = ahora
            }
        } else if (!esIncorrecta && alertaActiva) {
            alertaActiva = false
            ultimaAlertaSonora = 0L
            gestorSonido.detenerSonido()
        }

        _estado.update {
            it.copy(
                esCorrecta = !esIncorrecta,
                mensaje = mensaje,
                alertaPorCamara = alertaCamara
            )
        }
    }
}
