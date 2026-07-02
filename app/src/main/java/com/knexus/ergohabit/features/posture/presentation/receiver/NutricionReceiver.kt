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
import com.knexus.ergohabit.core.database.dao.NutricionDao
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class NutricionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var nutricionDao: NutricionDao

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            reprogramarAlertas(context)
            return
        }

        val tipo = intent.getStringExtra("tipoComida") ?: return
        mostrarNotificacionConSonidoLargo(context, tipo)
    }

    private fun mostrarNotificacionConSonidoLargo(context: Context, tipo: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "nutricion_recordatorio_v3" // Nuevo ID para asegurar silencio en el canal
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Recordatorios de Comida"
            val channel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Alertas de nutrición de ErgoHabit"
                enableVibration(true)
                // HACEMOS EL CANAL SILENCIOSO porque el sonido lo manejaremos manualmente por 6s
                setSound(null, null)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("irANutricion", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context, tipo.hashCode(), activityIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "¡Hora de tu $tipo! 🥗"
        val message = "Faltan 10 minutos para tu horario de comida establecido."

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setSound(null) // Notificación silenciosa
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(tipo.hashCode(), notification)

        // Lógica para sonido de ALARMA de 6 segundos con COLA INTELIGENTE (Turnos)
        val prefs = context.getSharedPreferences("ergo_sound_sync", Context.MODE_PRIVATE)
        val now = System.currentTimeMillis()
        val lastSoundEnd = prefs.getLong("ergo_last_sound_end", 0L)
        
        val startTime = Math.max(now, lastSoundEnd + 2000)
        val waitTime = startTime - now

        prefs.edit().putLong("ergo_last_sound_end", startTime + 6000).apply()

        val result = goAsync()
        CoroutineScope(Dispatchers.Main).launch {
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
            } finally {
                result.finish()
            }
        }
    }

    private fun reprogramarAlertas(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val config = nutricionDao.getNutricionConfig().first()
            if (config != null) {
                val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                programar(context, am, config.horaDesayuno, "DESAYUNO", 300)
                programar(context, am, config.horaComida, "COMIDA", 301)
                programar(context, am, config.horaCena, "CENA", 302)
            }
        }
    }

    private fun programar(context: Context, am: AlarmManager, hora: String, tipo: String, code: Int) {
        if (hora.isBlank() || hora == "00:00" || hora == "--:--") return
        try {
            val partes = hora.split(":")
            val h = partes[0].toInt()
            val m = partes[1].take(2).toInt()

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
        } catch (e: Exception) {}
    }
}
