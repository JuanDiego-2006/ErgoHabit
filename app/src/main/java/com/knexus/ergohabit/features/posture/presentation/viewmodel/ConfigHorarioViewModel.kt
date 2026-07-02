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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ConfigHorarioViewModel @Inject constructor(
    private val getSuenoDashboardUseCase: GetSuenoDashboardUseCase,
    private val configurarHorarioSuenoUseCase: ConfigurarHorarioSuenoUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConfigHorarioUiState())
    val uiState: StateFlow<ConfigHorarioUiState> = _uiState.asStateFlow()

    init {
        cargarHorarioActual()
    }

    private fun cargarHorarioActual() {
        viewModelScope.launch {
            getSuenoDashboardUseCase().collect { result ->
                result.onSuccess { dashboard ->
                    val partes = dashboard.horaDespertarConfigurada.split(":")
                    val horas = partes.getOrNull(0)?.toIntOrNull() ?: 6
                    val minutos = partes.getOrNull(1)?.take(2)?.toIntOrNull() ?: 0
                    _uiState.update { it.copy(horas = horas, minutos = minutos) }
                }
            }
        }
    }

    fun incrementarHoras() {
        _uiState.update { estado -> estado.copy(horas = (estado.horas + 1) % 24) }
    }

    fun decrementarHoras() {
        _uiState.update { estado -> estado.copy(horas = (estado.horas - 1 + 24) % 24) }
    }

    fun incrementarMinutos() {
        _uiState.update { estado -> estado.copy(minutos = (estado.minutos + 5) % 60) }
    }

    fun decrementarMinutos() {
        _uiState.update { estado -> estado.copy(minutos = (estado.minutos - 5 + 60) % 60) }
    }

    fun guardarHorario() {
        viewModelScope.launch {
            val estado = _uiState.value
            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }
            configurarHorarioSuenoUseCase(estado.horaDespertar, estado.horaDormir)
                .onSuccess { msg ->
                    _uiState.update { it.copy(isLoading = false, successMessage = msg) }
                    programarAlarmas(estado.horaDormir, estado.horaDespertar)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    private fun programarAlarmas(horaDormir: String, horaDespertar: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        programarAlarma(alarmManager, horaDormir, -5, "RECORDATORIO_DORMIR", 200)
        programarAlarma(alarmManager, horaDespertar, 0, "ALARMA_DESPERTAR", 201)
    }

    private fun programarAlarma(alarmManager: AlarmManager, horaString: String, offsetMinutos: Int, tipo: String, requestCode: Int) {
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
    }

    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, error = null) }
    }
}
