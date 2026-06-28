package com.knexus.ergohabit.features.tareas.presentation.receiver

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
import com.knexus.ergohabit.features.tareas.domain.usecases.GetAlertaSaludUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Receptor de alarmas para las notificaciones de salud y fin de tarea.
 */
@AndroidEntryPoint
class AlertaSaludReceiver : BroadcastReceiver() {

    @Inject
    lateinit var getAlertaSaludUseCase: GetAlertaSaludUseCase

    override fun onReceive(context: Context, intent: Intent) {
        val idTarea = intent.getIntExtra("idTarea", -1)
        val esFinDeTarea = intent.getBooleanExtra("esFinDeTarea", false)
        if (idTarea == -1) return

        val channelId = "ergo_health_alerts"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alertas de Salud y Bienestar",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones para descansos y fin de tareas"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // --- MANTENEMOS LO QUE SÍ FUNCIONABA: FIN DE TAREA ---
        if (esFinDeTarea) {
            val activityIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("tareaCompletadaId", idTarea)
            }
            val pendingIntent = PendingIntent.getActivity(context, idTarea + 1000, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("¡Objetivo Cumplido! 🌟")
                .setContentText("Has terminado tu sesión de enfoque. ¡Excelente trabajo!")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify(idTarea + 1000, notification)
            return
        }

        // --- FLUJO: CORRECCIÓN PARA ALERTA DE SALUD (30 MIN) ---
        // 1. Mostrar notificación de aviso inmediato
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("¡Momento de Salud! 🧘")
            .setContentText("Es hora de revisar tu postura y estirar.")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)

        notificationManager.notify(idTarea, builder.build())

        // 2. Actualizar con info real de la API
        CoroutineScope(Dispatchers.IO).launch {
            getAlertaSaludUseCase(idTarea).onSuccess { info ->
                val activityIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra("frase", info.frase)
                    putExtra("accion", info.accion)
                }

                val pendingIntent = PendingIntent.getActivity(context, idTarea, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

                builder.setContentText(info.frase)
                       .setContentIntent(pendingIntent)
                
                notificationManager.notify(idTarea, builder.build())
                
                // Programar la siguiente
                programarSiguienteAlerta(context, idTarea)
            }.onFailure {
                programarSiguienteAlerta(context, idTarea)
            }
        }
    }

    private fun programarSiguienteAlerta(context: Context, idTarea: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val nextIntent = Intent(context, AlertaSaludReceiver::class.java).apply {
            putExtra("idTarea", idTarea)
            putExtra("esFinDeTarea", false)
        }
        val pendingIntent = PendingIntent.getBroadcast(context, idTarea, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val triggerTime = System.currentTimeMillis() + (30 * 60 * 1000)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        } else {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    }
}
