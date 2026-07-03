package com.knexus.ergohabit.features.posture.presentation.receiver

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import com.knexus.ergohabit.MainActivity
import com.knexus.ergohabit.core.database.dao.AguaDao
import com.knexus.ergohabit.core.database.dao.NutricionDao
import com.knexus.ergohabit.core.database.dao.SuenoDao
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class HidratacionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var suenoDao: SuenoDao

    @Inject
    lateinit var nutricionDao: NutricionDao

    @Inject
    lateinit var aguaDao: AguaDao

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            programarSiguienteAlarma(context, force = true)
            return
        }

        val result = goAsync()
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val agua = withContext(Dispatchers.IO) { aguaDao.getDashboard().first() }
                val sueno = withContext(Dispatchers.IO) { suenoDao.getSuenoDashboard().first() }
                val nutricion = withContext(Dispatchers.IO) { nutricionDao.getNutricionConfig().first() }
                
                // 0. VERIFICAR SI LAS NOTIFICACIONES ESTÁN ACTIVAS
                if (agua?.notificacionesActivas == false) return@launch

                val ahora = Calendar.getInstance()
                val horaActual = ahora.get(Calendar.HOUR_OF_DAY)
                val minutoActual = ahora.get(Calendar.MINUTE)
                val tiempoActualMinutos = horaActual * 60 + minutoActual

                // 1. RANGO DE SUEÑO (No molestar si duerme)
                var horaInicio = 8
                var horaFin = 22
                sueno?.let {
                    try {
                        horaInicio = it.horaDespertarConfigurada.split(":")[0].toInt()
                        horaFin = it.horaDormirConfigurada.split(":")[0].toInt()
                    } catch (_: Exception) {}
                }

                if (horaActual in horaInicio until horaFin) {
                    
                    // 2. REGLA DE PRIORIDAD DE COMIDA
                    var debeSaltarPorComida = false
                    nutricion?.let {
                        val comidas = listOf(it.horaDesayuno, it.horaComida, it.horaCena)
                        for (horaComidaStr in comidas) {
                            if (horaComidaStr.isBlank() || horaComidaStr == "--:--") continue
                            try {
                                val partes = horaComidaStr.split(":")
                                val horaC = partes[0].toInt()
                                val minC = partes[1].take(2).toInt()
                                val tiempoComidaMinutos = horaC * 60 + minC
                                
                                // Si falta menos de 20 min para comer o pasaron menos de 10 min de la alerta de comida
                                if (tiempoActualMinutos in (tiempoComidaMinutos - 20)..(tiempoComidaMinutos + 10)) {
                                    debeSaltarPorComida = true
                                    break
                                }
                            } catch (_: Exception) {}
                        }
                    }

                    if (!debeSaltarPorComida) {
                        mostrarNotificacionConSonidoLargo(context)
                    }
                }
            } finally {
                programarSiguienteAlarma(context, force = true)
                result.finish()
            }
        }
    }

    private suspend fun mostrarNotificacionConSonidoLargo(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "hidratacion_recordatorio_v2" // Nuevo ID para asegurar silencio
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Recordatorios de Hidratación"
            val channel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Alertas de hidratación de ErgoHabit"
                enableVibration(true)
                setSound(null, null) // Silencioso para manejo manual
            }
            notificationManager.createNotificationChannel(channel)
        }

        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("irAHidratacion", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 400, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("¡Hora de hidratarse! 💧")
            .setContentText("Recuerda beber un vaso de agua para mantener tu cuerpo funcionando al 100%.")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setSound(null)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(400, notification)
        ejecutarSonidoSincronizado(context)
    }

    private suspend fun ejecutarSonidoSincronizado(context: Context) {
        val prefs = context.getSharedPreferences("ergo_sound_sync", Context.MODE_PRIVATE)
        val now = System.currentTimeMillis()
        
        // --- SISTEMA DE TURNOS (2 seg de silencio entre alarmas) ---
        val lastSoundEnd = prefs.getLong("ergo_last_sound_end", 0L)
        val startTime = Math.max(now, lastSoundEnd + 2000)
        val waitTime = startTime - now

        prefs.edit().putLong("ergo_last_sound_end", startTime + 6000).apply()

        try {
            if (waitTime > 0) delay(waitTime) 

            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val ringtone = RingtoneManager.getRingtone(context, uri).apply {
                audioAttributes = android.media.AudioAttributes.Builder()
                    .setUsage(android.media.AudioAttributes.USAGE_ALARM)
                    .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            }
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            
            ringtone?.play()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 500, 500), 0))
            } else {
                vibrator.vibrate(longArrayOf(0, 500, 500), 0)
            }

            delay(6000)
            
            ringtone?.stop()
            vibrator.cancel()
        } catch (e: Exception) {
        }
    }

    companion object {
        fun programarSiguienteAlarma(context: Context, force: Boolean = false) {
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, HidratacionReceiver::class.java)
            
            if (!force) {
                val existing = PendingIntent.getBroadcast(
                    context, 400, intent, 
                    PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
                )
                if (existing != null) return 
            }

            val pi = PendingIntent.getBroadcast(
                context, 400, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Acceso a DAOs para programación inteligente
            val suenoDao = EntryPointAccessors.fromApplication(context, DaoEntryPoint::class.java).suenoDao()
            
            CoroutineScope(Dispatchers.IO).launch {
                val sueno = suenoDao.getSuenoDashboard().first()
                val cal = Calendar.getInstance()
                val horaActual = cal.get(Calendar.HOUR_OF_DAY)

                var horaInicio = 8
                var horaFin = 22

                sueno?.let {
                    try {
                        horaInicio = it.horaDespertarConfigurada.split(":")[0].toInt()
                        horaFin = it.horaDormirConfigurada.split(":")[0].toInt()
                    } catch (_: Exception) {}
                }

                if (horaActual >= horaFin) {
                    // Hora de dormir: Programar para MAÑANA a la hora de despertar + 15 MINUTOS
                    cal.add(Calendar.DAY_OF_YEAR, 1)
                    cal.set(Calendar.HOUR_OF_DAY, horaInicio)
                    cal.set(Calendar.MINUTE, 15)
                    cal.set(Calendar.SECOND, 0)
                } else if (horaActual < horaInicio) {
                    // Aún no despierta: Programar para HOY a la hora de despertar + 15 MINUTOS
                    cal.set(Calendar.HOUR_OF_DAY, horaInicio)
                    cal.set(Calendar.MINUTE, 15)
                    cal.set(Calendar.SECOND, 0)
                } else {
                    // Durante el día: Cada 2 horas
                    cal.add(Calendar.HOUR_OF_DAY, 2)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && am.canScheduleExactAlarms()) {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pi)
                } else {
                    am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pi)
                }
            }
        }
    }
}

@dagger.hilt.EntryPoint
@dagger.hilt.InstallIn(dagger.hilt.components.SingletonComponent::class)
interface DaoEntryPoint {
    fun suenoDao(): SuenoDao
    fun nutricionDao(): NutricionDao
    fun aguaDao(): AguaDao
}
