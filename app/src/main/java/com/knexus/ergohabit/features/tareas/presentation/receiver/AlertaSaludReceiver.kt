package com.knexus.ergohabit.features.tareas.presentation.receiver

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.media.RingtoneManager
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import com.knexus.ergohabit.MainActivity
import com.knexus.ergohabit.features.tareas.domain.usecases.GetAlertaSaludUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 */
@AndroidEntryPoint
class AlertaSaludReceiver : BroadcastReceiver() {

    @Inject
    lateinit var getAlertaSaludUseCase: GetAlertaSaludUseCase

    @Inject
    lateinit var pausarTareaUseCase: com.knexus.ergohabit.features.tareas.domain.usecases.PausarTareaUseCase

    override fun onReceive(context: Context, intent: Intent) {
        val idTarea = intent.getIntExtra("idTarea", -1)
        val esFinDeTarea = intent.getBooleanExtra("esFinDeTarea", false)
        if (idTarea == -1) return

        val channelId = "ergo_health_alerts_v2" // Nuevo ID para asegurar silencio y sonido manual
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = if (esFinDeTarea) "Fin de Tareas" else "Alertas de Salud"
            val channel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Notificaciones para descansos y fin de tareas"
                enableVibration(true)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                setSound(null, null) // Silencioso para manejar el sonido manualmente
            }
            notificationManager.createNotificationChannel(channel)
        }

        // --- MANTENEMOS LO QUE SÍ FUNCIONABA: FIN DE TAREA ---
        if (esFinDeTarea) {
            val activityIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("tareaCompletadaId", idTarea)
            }
            // OFFSET 6000 para Fin de Tarea (Evita choques con Nutrición/Sueño/Hidratación)
            val notificationId = idTarea + 6000
            val pendingIntent = PendingIntent.getActivity(context, notificationId, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("¡Objetivo Cumplido! 🌟")
                .setContentText("Has terminado tu sesión de enfoque. ¡Excelente trabajo!")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setSound(null)
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify(notificationId, notification)
            ejecutarSonidoLargo(context)
            return
        }

        // 1. Mostrar notificación de aviso inmediato (LO PRIMERO)
        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("idTareaAlerta", idTarea)
        }
        // OFFSET 5000 para Alertas de Salud
        val notificationId = idTarea + 5000
        val pendingIntent = PendingIntent.getActivity(context, notificationId, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("¡Momento de Salud! 🧘")
            .setContentText("Es hora de revisar tu postura y estirar.")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setSound(null)
            .setOngoing(true)
            .setContentIntent(pendingIntent)

        notificationManager.notify(notificationId, builder.build())
        ejecutarSonidoLargo(context)

        // 2. Mandar a pausar y traer info real en segundo plano
        val result = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                pausarTareaUseCase(idTarea)
                
                // Cancelar alarma de fin usando el offset correcto
                val intentFin = Intent(context, AlertaSaludReceiver::class.java)
                val pendingFin = PendingIntent.getBroadcast(context, idTarea + 6000, intentFin, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
                if (pendingFin != null) {
                    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    alarmManager.cancel(pendingFin)
                }

                getAlertaSaludUseCase(idTarea).onSuccess { info ->
                    activityIntent.putExtra("frase", info.frase)
                    activityIntent.putExtra("accion", info.accion)
                    val updatedPendingIntent = PendingIntent.getActivity(context, notificationId, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                    builder.setContentText(info.frase)
                           .setContentIntent(updatedPendingIntent)
                           .setOngoing(false)
                    notificationManager.notify(notificationId, builder.build())
                }
            } finally {
                result.finish()
            }
        }
    }

    private fun ejecutarSonidoLargo(context: Context) {
        val prefs = context.getSharedPreferences("ergo_sound_sync", Context.MODE_PRIVATE)
        val now = System.currentTimeMillis()
        
        // --- SISTEMA DE TURNOS (Soporta 2, 3 o más notificaciones) ---
        // Buscamos cuándo terminó el último sonido. Si fue hace mucho, empezamos ahora.
        val lastSoundEnd = prefs.getLong("ergo_last_sound_end", 0L)
        
        // El tiempo de inicio será: el máximo entre 'ahora' y 'el fin del anterior + 2 segundos'
        val startTime = Math.max(now, lastSoundEnd + 2000) 
        val waitTime = startTime - now
        
        // Registramos que nosotros terminaremos 6 segundos después de empezar
        prefs.edit().putLong("ergo_last_sound_end", startTime + 6000).apply()

        val result = goAsync()
        CoroutineScope(Dispatchers.Main).launch {
            try {
                if (waitTime > 0) delay(waitTime) // Espera su turno si hay otra sonando

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

                delay(6000) // 6 segundos de sonido
                
                ringtone?.stop()
                vibrator.cancel()
            } catch (e: Exception) {
            } finally {
                result.finish()
            }
        }
    }

    // Nota: La siguiente alerta se programa desde TareaViewModel al dar clic en "¡Listo!"
}
