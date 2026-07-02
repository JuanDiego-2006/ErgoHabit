package com.knexus.ergohabit.features.posture.presentation.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knexus.ergohabit.features.posture.domain.usecase.ConfigurarHorarioSuenoUseCase
import com.knexus.ergohabit.features.posture.domain.usecase.GetSuenoDashboardUseCase
import com.knexus.ergohabit.features.posture.presentation.receiver.SuenoReceiver
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class RetrasoSuenoViewModel @Inject constructor(
    private val getSuenoDashboardUseCase: GetSuenoDashboardUseCase,
    private val configurarHorarioSuenoUseCase: ConfigurarHorarioSuenoUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(RetrasoSuenoUiState())
    val uiState: StateFlow<RetrasoSuenoUiState> = _uiState.asStateFlow()

    private var horaDespertarActual = ""
    private var horaDormirActual = ""

    init {
        cargarDatosActuales()
    }

    private fun cargarDatosActuales() {
        viewModelScope.launch {
            val result = getSuenoDashboardUseCase().first().getOrNull()
            if (result != null) {
                horaDespertarActual = result.horaDespertarConfigurada
                horaDormirActual = result.horaDormirConfigurada
                
                // Calculamos las horas planificadas actuales
                _uiState.update { it.copy(horasActuales = result.horasPlanificadas) }
            }
        }
    }

    fun incrementarHoras() {
        _uiState.update { estado ->
            estado.copy(horasRetraso = (estado.horasRetraso + 1).coerceAtMost(3))
        }
    }

    fun decrementarHoras() {
        _uiState.update { estado ->
            estado.copy(horasRetraso = (estado.horasRetraso - 1).coerceAtLeast(0))
        }
    }

    fun confirmar() {
        val s = _uiState.value
        if (horaDormirActual.isBlank() || horaDespertarActual.isBlank()) return

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                // Calcular nueva hora de dormir
                // "Retrasar" significa dormir MÁS TARDE.
                // Ej: Si duermo a las 22:00 y retraso 1h, ahora duermo a las 23:00.
                val partes = horaDormirActual.split(":")
                var h = partes[0].toInt()
                val m = partes[1].take(2).toInt()

                // Sumamos las horas de retraso
                h = (h + s.horasRetraso) % 24
                
                val nuevaHoraDormir = String.format(java.util.Locale.ROOT, "%02d:%02d", h, m)

                configurarHorarioSuenoUseCase(horaDespertarActual, nuevaHoraDormir)
                    .onSuccess {
                        _uiState.update { it.copy(isLoading = false, success = true) }
                        programarAlarmas(nuevaHoraDormir, horaDespertarActual)
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(isLoading = false, error = error.message) }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al calcular el horario") }
            }
        }
    }

    private fun programarAlarmas(horaDormir: String, horaDespertar: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        programarAlarma(alarmManager, horaDormir, -5, "RECORDATORIO_DORMIR", 200)
        programarAlarma(alarmManager, horaDespertar, 0, "ALARMA_DESPERTAR", 201)
    }

    private fun programarAlarma(alarmManager: AlarmManager, horaString: String, offsetMinutos: Int, tipo: String, requestCode: Int) {
        try {
            val partes = horaString.split(":")
            val h = partes.getOrNull(0)?.toIntOrNull() ?: return
            val m = partes.getOrNull(1)?.take(2)?.toIntOrNull() ?: return

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, h)
                set(Calendar.MINUTE, m)
                set(Calendar.SECOND, 0)
                add(Calendar.MINUTE, offsetMinutos)
                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            val intent = Intent(context, SuenoReceiver::class.java).apply {
                putExtra("tipo", tipo)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            }
        } catch (e: Exception) {}
    }
}
