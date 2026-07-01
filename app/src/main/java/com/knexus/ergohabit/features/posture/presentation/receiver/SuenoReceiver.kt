package com.knexus.ergohabit.features.posture.presentation.receiver

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.knexus.ergohabit.MainActivity
import com.knexus.ergohabit.core.database.dao.SuenoDao
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

        val tipo = intent.getStringExtra("tipo") ?: return
        // ... (resto del código de notificación se mantiene igual)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "sueno_notifications"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notificaciones de Sueño"
            val channel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Recordatorios para dormir y despertar"
                enableVibration(true)
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
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .apply {
                if (tipo == "ALARMA_DESPERTAR") {
                    setFullScreenIntent(pendingIntent, true)
                    setOngoing(true)
                    // Sonido y vibración infinitos hasta que se abra
                    val alarmSound = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_ALARM)
                    setSound(alarmSound)
                }
            }
            .build().apply {
                if (tipo == "ALARMA_DESPERTAR") {
                    flags = flags or NotificationCompat.FLAG_INSISTENT
                }
            }

        notificationManager.notify(if (tipo == "RECORDATORIO_DORMIR") 200 else 201, notification)
    }

    private fun reprogramarAlarmas(context: Context) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            val entity = suenoDao.getSuenoDashboard().first()
            if (entity != null) {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                
                // Programar Dormir (-5 min)
                programar(context, alarmManager, entity.horaDormirConfigurada, -5, "RECORDATORIO_DORMIR", 200)
                
                // Programar Despertar (0 offset)
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
