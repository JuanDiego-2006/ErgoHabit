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
import com.knexus.ergohabit.core.database.dao.NutricionDao
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "nutricion_notifications"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notificaciones de Nutrición"
            val channel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Recordatorios de comida"
                enableVibration(true)
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
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(tipo.hashCode(), notification)
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
}
