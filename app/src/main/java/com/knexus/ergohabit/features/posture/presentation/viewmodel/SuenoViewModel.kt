package com.knexus.ergohabit.features.posture.presentation.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knexus.ergohabit.features.posture.domain.usecase.GetSuenoDashboardUseCase
import com.knexus.ergohabit.features.posture.domain.usecase.RegistrarDespertarUseCase
import com.knexus.ergohabit.features.posture.domain.usecase.SetAlertaSuenoActivaUseCase
import com.knexus.ergohabit.features.posture.domain.usecase.SetNotificacionesSuenoUseCase
import com.knexus.ergohabit.features.posture.presentation.receiver.SuenoReceiver
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class SuenoViewModel @Inject constructor(
    private val getSuenoDashboardUseCase: GetSuenoDashboardUseCase,
    private val registrarDespertarUseCase: RegistrarDespertarUseCase,
    private val setAlertaSuenoActivaUseCase: SetAlertaSuenoActivaUseCase,
    private val setNotificacionesSuenoUseCase: SetNotificacionesSuenoUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _mensajesUi = MutableStateFlow<Pair<String?, String?>>(null to null)
    
    private val _uiState = MutableStateFlow(SuenoUiState(isLoading = false))
    val uiState: StateFlow<SuenoUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                getSuenoDashboardUseCase(),
                _mensajesUi
            ) { result, mensajes ->
                val response = result.getOrNull()
                if (response != null) {
                    SuenoUiState(
                        horasDormidas = response.horasDormidasReales.toFloat(),
                        horasRecomendadas = if (response.horasPlanificadas > 0) response.horasPlanificadas.toFloat() else 8f,
                        calidad = when {
                            response.porcentajeCumplimiento >= 80 -> "Buena"
                            response.porcentajeCumplimiento >= 50 -> "Moderada"
                            else -> "Baja"
                        },
                        horaDormir = response.horaDormirConfigurada,
                        horaDespertar = response.horaDespertarConfigurada,
                        alarmaActivada = response.despertoATiempo,
                        fraseMotivacional = response.fraseMotivacional,
                        tips = response.tipsSueno,
                        isLoading = false,
                        mostrarAlarma = response.isAlarmActiveLocal,
                        reproducirSonido = response.isSoundEnabledLocal,
                        notificacionesHabilitadas = response.notificacionesHabilitadasLocal,
                        successMessage = mensajes.first,
                        error = mensajes.second ?: result.exceptionOrNull()?.message
                    )
                } else null
            }.collect { nuevoEstado ->
                if (nuevoEstado != null && nuevoEstado.horaDormir.isNotEmpty()) {
                    _uiState.value = nuevoEstado
                }
            }
        }
        startAutoRefresh()
    }

    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                delay(60_000)
                cargarDashboard()
            }
        }
    }

    fun cargarDashboard() {
        viewModelScope.launch {
            getSuenoDashboardUseCase().collect() 
        }
    }

    fun registrarDespertar() {
        viewModelScope.launch {
            registrarDespertarUseCase().onSuccess { msg ->
                _mensajesUi.value = msg to null
            }.onFailure { error ->
                _mensajesUi.value = null to error.message
            }
        }
    }

    fun mostrarAlarma(show: Boolean) {
        viewModelScope.launch {
            setAlertaSuenoActivaUseCase(show)
        }
    }

    fun toggleNotificaciones() {
        viewModelScope.launch {
            val actual = _uiState.value.notificacionesHabilitadas
            val nuevo = !actual
            setNotificacionesSuenoUseCase(nuevo)
            
            if (nuevo) {
                // Programar alarmas
                programarAlarmas(_uiState.value.horaDormir, _uiState.value.horaDespertar)
                _mensajesUi.value = "Notificaciones activadas" to null
            } else {
                // Cancelar alarmas
                cancelarAlarmas()
                _mensajesUi.value = "Notificaciones desactivadas" to null
            }
        }
    }

    private fun programarAlarmas(horaDormir: String, horaDespertar: String) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        programar(am, horaDormir, -5, "RECORDATORIO_DORMIR", 200)
        programar(am, horaDespertar, 0, "ALARMA_DESPERTAR", 201)
    }

    private fun programar(am: AlarmManager, hStr: String, offset: Int, tipo: String, code: Int) {
        if (hStr.isBlank() || hStr == "00:00" || hStr == "--:--" || hStr == "Sin establecer") return
        try {
            val clean = hStr.trim().uppercase()
            val partesBase = clean.split(" ")[0].split(":")
            var h = partesBase[0].toInt()
            val m = partesBase[1].take(2).toInt()

            // Normalización a 24h para el Calendar de Android
            if (clean.contains("PM") && h < 12) h += 12
            if (clean.contains("AM") && h == 12) h = 0

            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, h)
                set(Calendar.MINUTE, m)
                set(Calendar.SECOND, 0)
                add(Calendar.MINUTE, offset)
                if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
            }
            val intent = Intent(context, SuenoReceiver::class.java).apply { putExtra("tipo", tipo) }
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
        val intent = Intent(context, SuenoReceiver::class.java)
        val pi1 = PendingIntent.getBroadcast(context, 200, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val pi2 = PendingIntent.getBroadcast(context, 201, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        am.cancel(pi1)
        am.cancel(pi2)
    }

    fun clearMessages() {
        _mensajesUi.value = null to null
    }
}
