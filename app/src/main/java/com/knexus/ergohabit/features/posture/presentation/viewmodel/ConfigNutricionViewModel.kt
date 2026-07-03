package com.knexus.ergohabit.features.posture.presentation.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knexus.ergohabit.features.posture.domain.usecase.ConfigurarHorariosNutricionUseCase
import com.knexus.ergohabit.features.posture.domain.usecase.GetNutricionDashboardUseCase
import com.knexus.ergohabit.features.posture.presentation.receiver.NutricionReceiver
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
class ConfigNutricionViewModel @Inject constructor(
    private val getNutricionDashboardUseCase: GetNutricionDashboardUseCase,
    private val configurarHorariosNutricionUseCase: ConfigurarHorariosNutricionUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConfigNutricionUiState())
    val uiState: StateFlow<ConfigNutricionUiState> = _uiState.asStateFlow()

    init {
        cargarHorarios()
    }

    private fun cargarHorarios() {
        viewModelScope.launch {
            getNutricionDashboardUseCase().collect { result ->
                result.onSuccess { dashboard ->
                    _uiState.update {
                        it.copy(
                            horaDesayuno = normalizarA24h(dashboard.horaDesayunoConfigurada),
                            horaComida = normalizarA24h(dashboard.horaComidaConfigurada),
                            horaCena = normalizarA24h(dashboard.horaCenaConfigurada),
                            notificacionesHabilitadas = dashboard.notificacionesHabilitadasLocal
                        )
                    }
                }
            }
        }
    }

    private fun normalizarA24h(hora: String): String {
        if (hora.isBlank() || hora == "--:--" || hora == "Sin establecer" || hora == "00:00") return ""
        val clean = hora.trim().uppercase()
        if (!clean.contains("AM") && !clean.contains("PM")) {
            return try {
                val partes = clean.split(" ")[0].split(":")
                String.format(Locale.ROOT, "%02d:%02d", partes[0].toInt(), partes[1].take(2).toInt())
            } catch (e: Exception) { clean.take(5) }
        }
        return try {
            val p = clean.split(" ")
            val t = p[0].split(":")
            var h = t[0].toInt()
            if (clean.contains("PM") && h < 12) h += 12
            if (clean.contains("AM") && h == 12) h = 0
            String.format(Locale.ROOT, "%02d:%02d", h, t[1].take(2).toInt())
        } catch (e: Exception) { clean.take(5) }
    }

    fun abrirEditor(comida: String, emoji: String, horaActual: String) {
        _uiState.update { it.copy(
            showSheet = true,
            comidaEditando = comida,
            emojiEditando = emoji,
            error = null,
            horaTemp = normalizarA24h(horaActual).ifBlank {
                when(comida) {
                    "Desayuno" -> "07:30"
                    "Comida" -> "14:30"
                    "Cena" -> "20:30"
                    else -> "08:00"
                }
            }
        ) }
    }

    fun cerrarEditor() {
        _uiState.update { it.copy(showSheet = false, error = null) }
    }

    fun onHoraTempChange(nuevaHora: String) {
        _uiState.update { it.copy(horaTemp = normalizarA24h(nuevaHora)) }
    }

    fun confirmarHora() {
        val s = _uiState.value
        val nueva = normalizarA24h(s.horaTemp)
        
        val d = if (s.comidaEditando == "Desayuno") nueva else normalizarA24h(s.horaDesayuno).ifBlank { "07:30" }
        val c = if (s.comidaEditando == "Comida") nueva else normalizarA24h(s.horaComida).ifBlank { "14:30" }
        val ce = if (s.comidaEditando == "Cena") nueva else normalizarA24h(s.horaCena).ifBlank { "20:30" }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }
            configurarHorariosNutricionUseCase(d, c, ce)
                .onSuccess { msg ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        successMessage = msg,
                        showSheet = false,
                        horaDesayuno = d,
                        horaComida = c,
                        horaCena = ce
                    ) }
                    programarAlarmasNutricion(d, c, ce)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun guardar() {
        _uiState.update { it.copy(successMessage = "Horarios guardados") }
    }

    private fun programarAlarmasNutricion(d: String, c: String, ce: String) {
        if (!_uiState.value.notificacionesHabilitadas) return
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        programar(am, d, "DESAYUNO", 300)
        programar(am, c, "COMIDA", 301)
        programar(am, ce, "CENA", 302)
    }

    private fun programar(am: AlarmManager, hStr: String, tipo: String, code: Int) {
        if (hStr.isBlank() || hStr == "00:00" || hStr == "--:--" || hStr == "Sin establecer") return
        try {
            val clean = hStr.trim().uppercase()
            val partesBase = clean.split(" ")[0].split(":")
            var h = partesBase[0].toInt()
            val m = partesBase[1].take(2).toInt()

            if (clean.contains("PM") && h < 12) h += 12
            if (clean.contains("AM") && h == 12) h = 0

            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, h)
                set(Calendar.MINUTE, m)
                set(Calendar.SECOND, 0)
                add(Calendar.MINUTE, -10)
                if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
            }
            val i = Intent(context, NutricionReceiver::class.java).apply { putExtra("tipoComida", tipo) }
            val pi = PendingIntent.getBroadcast(context, code, i, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && am.canScheduleExactAlarms()) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pi)
            } else {
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pi)
            }
        } catch (e: Exception) {}
    }

    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, error = null) }
    }
}
