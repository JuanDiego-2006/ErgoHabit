package com.knexus.ergohabit.features.posture.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knexus.ergohabit.core.session.SessionManager
import com.knexus.ergohabit.features.posture.data.datasource.api.HabitosApi
import com.knexus.ergohabit.features.posture.domain.GestorMonitoreoPostura
import com.knexus.ergohabit.features.progreso.data.datasource.api.ProgresoDiarioApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PosturaViewModel @Inject constructor(
    private val gestorMonitoreo: GestorMonitoreoPostura,
    private val progresoDiarioApi: ProgresoDiarioApi,
    private val habitosApi: HabitosApi,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _estadoUi = MutableStateFlow(PosturaUiState())
    val estadoUi: StateFlow<PosturaUiState> = _estadoUi.asStateFlow()

    init {
        refrescarDashboard()
        viewModelScope.launch {
            gestorMonitoreo.estado.collect { monitoreo ->
                _estadoUi.update {
                    it.copy(
                        anguloPitch = monitoreo.anguloPitch,
                        anguloRoll = monitoreo.anguloRoll,
                        esCorrecta = monitoreo.esCorrecta,
                        mensaje = monitoreo.mensaje,
                        estaMonitoreando = monitoreo.activo,
                        conteoVibraciones = monitoreo.conteoAlertas,
                        gradosDisplay = monitoreo.gradosDisplay,
                        mensajeCamara = monitoreo.mensajeCamara,
                        camaraActiva = monitoreo.camaraActiva,
                        alertaPorCamara = monitoreo.alertaPorCamara
                    )
                }
            }
        }
    }

    fun refrescarDashboard() {
        val userId = sessionManager.fetchUserId() ?: return
        viewModelScope.launch {
            _estadoUi.update { it.copy(cargandoDashboard = true) }
            try {
                val progreso = progresoDiarioApi.getProgresoDiario(userId)
                val nutricion = habitosApi.obtenerDashboardNutricion()
                val pctNutricion = nutricion.porcentajeCumplimiento.coerceIn(0, 100)

                val habitos = listOf(
                    ResumenHabitoUi(
                        emoji = "💧",
                        nombre = "Agua",
                        meta = "Meta: ${progreso.metaAguaMl} ml",
                        pct = progreso.porcentajeAgua.toInt().coerceIn(0, 100),
                        completado = progreso.porcentajeAgua >= 100
                    ),
                    ResumenHabitoUi(
                        emoji = "🌙",
                        nombre = "Sueño",
                        meta = "Meta: 8 horas",
                        pct = progreso.porcentajeSueno.toInt().coerceIn(0, 100),
                        completado = progreso.porcentajeSueno >= 100
                    ),
                    ResumenHabitoUi(
                        emoji = "🏃",
                        nombre = "Ejercicio",
                        meta = "Meta: ${progreso.metaEjercicioKm.toInt()} km",
                        pct = progreso.porcentajeEjercicio.toInt().coerceIn(0, 100),
                        completado = progreso.porcentajeEjercicio >= 100
                    ),
                    ResumenHabitoUi(
                        emoji = "🍎",
                        nombre = "Nutrición",
                        meta = "Meta: 3 comidas",
                        pct = pctNutricion,
                        completado = pctNutricion >= 100
                    )
                )
                val completados = habitos.count { it.completado }
                _estadoUi.update {
                    it.copy(
                        nombreUsuario = progreso.nombreUsuario,
                        habitosCompletados = completados,
                        habitosTotal = habitos.size,
                        rachaDias = progreso.rachaDias,
                        resumenHabitos = habitos,
                        cargandoDashboard = false
                    )
                }
            } catch (_: Exception) {
                _estadoUi.update { it.copy(cargandoDashboard = false) }
            }
        }
    }

    fun alternarMonitoreo() {
        if (gestorMonitoreo.estado.value.activo) gestorMonitoreo.detener()
        else gestorMonitoreo.iniciar()
    }
}
