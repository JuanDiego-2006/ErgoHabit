package com.knexus.ergohabit.features.posture.presentation.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knexus.ergohabit.features.posture.domain.repository.NutricionRepository
import com.knexus.ergohabit.features.posture.presentation.receiver.NutricionReceiver
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ConfigNutricionViewModel @Inject constructor(
    private val repository: NutricionRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConfigNutricionUiState())
    val uiState: StateFlow<ConfigNutricionUiState> = _uiState.asStateFlow()

    init {
        cargarHorarios()
    }

    private fun cargarHorarios() {
        viewModelScope.launch {
            repository.getNutricionDashboard().collect { result ->
                result.onSuccess { dashboard ->
                    _uiState.update {
                        it.copy(
                            horaDesayuno = if (dashboard.horaDesayunoConfigurada != "00:00" && dashboard.horaDesayunoConfigurada.isNotBlank()) dashboard.horaDesayunoConfigurada else "08:00",
                            horaComida = if (dashboard.horaComidaConfigurada != "00:00" && dashboard.horaComidaConfigurada.isNotBlank()) dashboard.horaComidaConfigurada else "14:00",
                            horaCena = if (dashboard.horaCenaConfigurada != "00:00" && dashboard.horaCenaConfigurada.isNotBlank()) dashboard.horaCenaConfigurada else "20:00"
                        )
                    }
                }
            }
        }
    }

    fun abrirEditor(comida: String, emoji: String, horaActual: String) {
        _uiState.update { it.copy(
            showSheet = true,
            comidaEditando = comida,
            emojiEditando = emoji,
            horaTemp = if (horaActual == "00:00" || horaActual.isBlank()) {
                when(comida) {
                    "Desayuno" -> "08:00"
                    "Comida" -> "14:00"
                    "Cena" -> "20:00"
                    else -> "08:00"
                }
            } else horaActual
        ) }
    }

    fun cerrarEditor() {
        _uiState.update { it.copy(showSheet = false) }
    }

    fun onHoraTempChange(nuevaHora: String) {
        _uiState.update { it.copy(horaTemp = nuevaHora) }
    }

    fun confirmarHora() {
        val s = _uiState.value
        _uiState.update {
            when (s.comidaEditando) {
                "Desayuno" -> it.copy(horaDesayuno = s.horaTemp, showSheet = false)
                "Comida" -> it.copy(horaComida = s.horaTemp, showSheet = false)
                "Cena" -> it.copy(horaCena = s.horaTemp, showSheet = false)
                else -> it.copy(showSheet = false)
            }
        }
    }

    fun guardar() {
        viewModelScope.launch {
            val s = _uiState.value
            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }
            repository.configurarHorarios(s.horaDesayuno, s.horaComida, s.horaCena)
                .onSuccess { msg ->
                    _uiState.update { it.copy(isLoading = false, successMessage = msg) }
                    programarAlarmasNutricion(s.horaDesayuno, s.horaComida, s.horaCena)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    private fun programarAlarmasNutricion(desayuno: String, comida: String, cena: String) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        programar(am, desayuno, "DESAYUNO", 300)
        programar(am, comida, "COMIDA", 301)
        programar(am, cena, "CENA", 302)
    }

    private fun programar(am: AlarmManager, hora: String, tipo: String, code: Int) {
        if (hora.isBlank() || hora == "00:00") return
        val partes = hora.split(":")
        val h = partes.getOrNull(0)?.toIntOrNull() ?: return
        val m = partes.getOrNull(1)?.toIntOrNull() ?: return

        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, h)
            set(Calendar.MINUTE, m)
            set(Calendar.SECOND, 0)
            add(Calendar.MINUTE, -10)
            if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
        }

        val intent = Intent(context, NutricionReceiver::class.java).apply { putExtra("tipoComida", tipo) }
        val pi = PendingIntent.getBroadcast(context, code, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && am.canScheduleExactAlarms()) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pi)
        } else {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pi)
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, error = null) }
    }
}
