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
import com.knexus.ergohabit.core.database.dao.SuenoDao
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class SuenoReceiver : BroadcastReceiver() {

    @Inject
    lateinit var suenoDao: SuenoDao

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            reprogramarAlarmas(context)
            return
        }

        if (intent.action == "com.knexus.ergohabit.SILENCIAR_ALARMA") {
            CoroutineScope(Dispatchers.IO).launch {
                suenoDao.setSoundStatus(false)
            }
            return
        }

        val tipo = intent.getStringExtra("tipo") ?: return
        
        // Verificamos si las notificaciones están habilitadas globalmente antes de hacer nada
        val resultAsync = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val entity = suenoDao.getSuenoDashboard().first()
                if (entity?.notificacionesHabilitadas == false) return@launch

                // PERSISTENCIA: Si es alarma de despertar, marcamos en Room que está activa
                if (tipo == "ALARMA_DESPERTAR") {
                    suenoDao.updateAlarmStatus(true)
                }

                // Mostrar la alerta (debe ejecutarse en el Main o usar el contexto original)
                mostrarAlertaSueno(context, tipo)
            } finally {
                resultAsync.finish()
            }
        }
    }

    private fun mostrarAlertaSueno(context: Context, tipo: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = if (tipo == "ALARMA_DESPERTAR") "sueno_alarma_insistente" else "sueno_recordatorio_v3"
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = if (tipo == "ALARMA_DESPERTAR") "Alarma de Despertar" else "Recordatorios de Sueño"
            val channel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Alertas de sueño de ErgoHabit"
                enableVibration(true)
                // Para el recordatorio de 6s, silenciamos el canal porque lo manejamos manualmente
                if (tipo == "RECORDATORIO_DORMIR") {
                    setSound(null, null)
                } else {
                    setSound(alarmUri, android.media.AudioAttributes.Builder()
                        .setUsage(android.media.AudioAttributes.USAGE_ALARM)
                        .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build())
                }
            }
            notificationManager.createNotificationChannel(channel)
        }

        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            if (tipo == "RECORDATORIO_DORMIR") {
                putExtra("irASueno", true)
            } else if (tipo == "ALARMA_DESPERTAR") {
                putExtra("mostrarAlarmaSueno", true)
            }
        }

        val deleteIntent = Intent(context, SuenoReceiver::class.java).apply {
            action = "com.knexus.ergohabit.SILENCIAR_ALARMA"
        }
        val deletePendingIntent = PendingIntent.getBroadcast(
            context, 202, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val pendingIntent = PendingIntent.getActivity(
            context, 
            if (tipo == "RECORDATORIO_DORMIR") 200 else 201, 
            activityIntent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (tipo == "RECORDATORIO_DORMIR") "¡Casi hora de dormir! 🌙" else "¡Es hora de despertar! 🌅"
        val message = if (tipo == "RECORDATORIO_DORMIR") "Faltan 5 minutos para tu hora de descanso." else "Buenos días! Tu jornada de estudio comienza ahora."

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setDeleteIntent(deletePendingIntent)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(if (tipo == "RECORDATORIO_DORMIR") null else alarmUri) // Silenciamos solo el recordatorio de 6s
            .apply {
                if (tipo == "ALARMA_DESPERTAR") {
                    setFullScreenIntent(pendingIntent, true)
                    setOngoing(true)
                }
            }
            .build().apply {
                if (tipo == "ALARMA_DESPERTAR") {
                    flags = flags or NotificationCompat.FLAG_INSISTENT
                }
            }

        notificationManager.notify(if (tipo == "RECORDATORIO_DORMIR") 200 else 201, notification)

        // Lógica de sonido largo de ALARMA (solo para recordatorio de dormir)
        // La alarma de despertar ya es insistente por FLAG_INSISTENT y seguirá hasta pulsar botón.
        if (tipo == "RECORDATORIO_DORMIR") {
            val result = goAsync()
            CoroutineScope(Dispatchers.Main).launch {
                try {
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

                    delay(6000) // Suena por 6 segundos
                    
                    ringtone?.stop()
                    vibrator.cancel()
                } catch (e: Exception) {
                } finally {
                    result.finish()
                }
            }
        }
    }

    private fun reprogramarAlarmas(context: Context) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            val entity = suenoDao.getSuenoDashboard().first()
            if (entity != null) {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                programar(context, alarmManager, entity.horaDormirConfigurada, -5, "RECORDATORIO_DORMIR", 200)
                programar(context, alarmManager, entity.horaDespertarConfigurada, 0, "ALARMA_DESPERTAR", 201)
            }
        }
    }

    private fun programar(context: Context, alarmManager: AlarmManager, hora: String, offset: Int, tipo: String, code: Int) {
        val partes = hora.split(":")
        val h = partes.getOrNull(0)?.toIntOrNull() ?: return
        val m = partes.getOrNull(1)?.toIntOrNull() ?: return

        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, h)
            set(Calendar.MINUTE, m)
            set(Calendar.SECOND, 0)
            add(Calendar.MINUTE, offset)
            if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
        }

        val i = Intent(context, SuenoReceiver::class.java).apply { putExtra("tipo", tipo) }
        val pi = PendingIntent.getBroadcast(context, code, i, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pi)
        } else {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pi)
        }
    }
}
