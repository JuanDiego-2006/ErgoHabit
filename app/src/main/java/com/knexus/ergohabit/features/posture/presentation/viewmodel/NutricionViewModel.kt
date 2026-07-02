package com.knexus.ergohabit.features.posture.presentation.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knexus.ergohabit.features.posture.domain.usecase.GetNutricionDashboardUseCase
import com.knexus.ergohabit.features.posture.domain.usecase.MarcarComidaUseCase
import com.knexus.ergohabit.features.posture.domain.usecase.SetNotificacionesNutricionUseCase
import com.knexus.ergohabit.features.posture.presentation.receiver.NutricionReceiver
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class NutricionViewModel @Inject constructor(
    private val getNutricionDashboardUseCase: GetNutricionDashboardUseCase,
    private val marcarComidaUseCase: MarcarComidaUseCase,
    private val setNotificacionesNutricionUseCase: SetNotificacionesNutricionUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(NutricionUiState())
    val uiState: StateFlow<NutricionUiState> = _uiState.asStateFlow()

    init {
        // En init, cargamos de forma silenciosa si ya tenemos algo para evitar parpadeo
        cargarDashboard(silent = true)
        startAutoRefresh()
    }

    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                delay(30_000)
                cargarDashboard(silent = true)
            }
        }
    }

    fun cargarDashboard(silent: Boolean = false) {
        viewModelScope.launch {
            // Solo mostramos loading si es la primerísima vez (estado vacío)
            if (!silent && _uiState.value.horaDesayuno == "--:--") {
                _uiState.update { it.copy(isLoading = true) }
            }
            
            getNutricionDashboardUseCase().collect { result ->
                result.onSuccess { respuesta ->
                    val completadas = listOf(
                        respuesta.chequeoDesayuno,
                        respuesta.chequeoComida,
                        respuesta.chequeoCena
                    ).count { it }
                    _uiState.update {
                        it.copy(
                            comidasCompletadas = completadas,
                            comidasObjetivo = 3,
                            desayunoCompletado = respuesta.chequeoDesayuno,
                            comidaCompletada = respuesta.chequeoComida,
                            cenaCompletada = respuesta.chequeoCena,
                            horaDesayuno = respuesta.horaDesayunoConfigurada,
                            horaComida = respuesta.horaComidaConfigurada,
                            horaCena = respuesta.horaCenaConfigurada,
                            mensajeFaltante = respuesta.mensajeFaltanteText,
                            fraseMotivacional = respuesta.fraseMotivacional,
                            tips = respuesta.tipsNutricion,
                            porcentajeBackend = respuesta.porcentajeCumplimiento,
                            notificacionesHabilitadas = respuesta.notificacionesHabilitadasLocal,
                            isLoading = false
                        )
                    }
                }.onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            }
        }
    }

    fun marcarComida(tipoComida: String, completada: Boolean) {
        viewModelScope.launch {
            _uiState.update { current ->
                when(tipoComida) {
                    "DESAYUNO" -> current.copy(desayunoCompletado = completada)
                    "COMIDA" -> current.copy(comidaCompletada = completada)
                    "CENA" -> current.copy(cenaCompletada = completada)
                    else -> current
                }
            }
            
            marcarComidaUseCase(tipoComida, completada).onSuccess { msg ->
                _uiState.update { it.copy(successMessage = msg) }
                cargarDashboard(silent = true)
            }.onFailure { error ->
                _uiState.update { it.copy(error = error.message) }
                cargarDashboard(silent = true)
            }
        }
    }

    fun toggleNotificaciones() {
        viewModelScope.launch {
            val actual = _uiState.value.notificacionesHabilitadas
            val nuevo = !actual
            setNotificacionesNutricionUseCase(nuevo)
            _uiState.update { it.copy(notificacionesHabilitadas = nuevo) }
            
            if (nuevo) {
                programarAlarmas(
                    _uiState.value.horaDesayuno,
                    _uiState.value.horaComida,
                    _uiState.value.horaCena
                )
                _uiState.update { it.copy(successMessage = "Notificaciones activadas") }
            } else {
                cancelarAlarmas()
                _uiState.update { it.copy(successMessage = "Notificaciones desactivadas") }
            }
        }
    }

    private fun programarAlarmas(desayuno: String, comida: String, cena: String) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        programar(am, desayuno, "DESAYUNO", 300)
        programar(am, comida, "COMIDA", 301)
        programar(am, cena, "CENA", 302)
    }

    private fun programar(am: AlarmManager, hora: String, tipo: String, code: Int) {
        if (hora.isBlank() || hora == "00:00" || hora == "--:--") return
        try {
            val partes = hora.split(":")
            val h = partes[0].toInt()
            val m = partes[1].take(2).toInt()

            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, h)
                set(Calendar.MINUTE, m)
                set(Calendar.SECOND, 0)
                add(Calendar.MINUTE, -10) // 10 min antes
                if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
            }

            val intent = Intent(context, NutricionReceiver::class.java).apply { putExtra("tipoComida", tipo) }
            val pi = PendingIntent.getBroadcast(context, code, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && am.canScheduleExactAlarms()) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pi)
            } else {
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pi)
            }
        } catch (_: Exception) {}
    }

    private fun cancelarAlarmas() {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NutricionReceiver::class.java)
        val pi1 = PendingIntent.getBroadcast(context, 300, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val pi2 = PendingIntent.getBroadcast(context, 301, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val pi3 = PendingIntent.getBroadcast(context, 302, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        am.cancel(pi1)
        am.cancel(pi2)
        am.cancel(pi3)
    }

    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, error = null) }
    }
}
