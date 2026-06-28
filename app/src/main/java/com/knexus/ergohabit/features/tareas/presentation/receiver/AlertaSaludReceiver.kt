package com.knexus.ergohabit.features.tareas.presentation.receiver

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
 * Receptor de alarmas para las notificaciones de salud cada 30 minutos.
 */
@AndroidEntryPoint
class AlertaSaludReceiver : BroadcastReceiver() {

    @Inject
    lateinit var getAlertaSaludUseCase: GetAlertaSaludUseCase

    override fun onReceive(context: Context, intent: Intent) {
        val idTarea = intent.getIntExtra("idTarea", -1)
        if (idTarea == -1) return

        // Canal de notificación para Android 8.0+
        val channelId = "ergo_health_alerts"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alertas de Salud y Bienestar",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Consultar API en segundo plano
        CoroutineScope(Dispatchers.IO).launch {
            getAlertaSaludUseCase(idTarea).onSuccess { info ->
                // Intent para abrir la App
                val activityIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra("mostrarAlertaId", idTarea)
                    putExtra("frase", info.frase)
                    putExtra("accion", info.accion)
                }

                val pendingIntent = PendingIntent.getActivity(
                    context, 
                    idTarea, 
                    activityIntent, 
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val notification = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("¡Es momento de un descanso!")
                    .setContentText(info.frase)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build()

                notificationManager.notify(idTarea, notification)
            }
        }
    }
}
