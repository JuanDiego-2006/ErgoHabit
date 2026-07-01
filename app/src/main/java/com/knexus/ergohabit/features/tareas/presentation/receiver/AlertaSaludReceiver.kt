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

        val channelId = "ergo_health_alerts"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = if (esFinDeTarea) "Fin de Tareas" else "Alertas de Salud"
            val channel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Notificaciones para descansos y fin de tareas"
                enableVibration(true)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
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

        // 1. Mostrar notificación de aviso inmediato (LO PRIMERO)
        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("idTareaAlerta", idTarea)
        }
        val pendingIntent = PendingIntent.getActivity(context, idTarea, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("¡Momento de Salud! 🧘")
            .setContentText("Es hora de revisar tu postura y estirar.")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setOngoing(true) // Para que no se borre sin querer hasta tener la info
            .setContentIntent(pendingIntent)

        notificationManager.notify(idTarea, builder.build())

        // 2. Mandar a pausar y traer info real en segundo plano
        val result = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                pausarTareaUseCase(idTarea)
                getAlertaSaludUseCase(idTarea).onSuccess { info ->
                    activityIntent.putExtra("frase", info.frase)
                    activityIntent.putExtra("accion", info.accion)
                    val updatedPendingIntent = PendingIntent.getActivity(context, idTarea, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                    builder.setContentText(info.frase)
                           .setContentIntent(updatedPendingIntent)
                           .setOngoing(false)
                    notificationManager.notify(idTarea, builder.build())
                }
            } finally {
                result.finish()
            }
        }
    }

    // Nota: La siguiente alerta se programa desde TareaViewModel al dar clic en "¡Listo!"
}
