package com.knexus.ergohabit.features.posture.presentation.services

import android.app.KeyguardManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.knexus.ergohabit.features.posture.domain.GestorMonitoreoPostura
import com.knexus.ergohabit.features.posture.domain.EstadoMonitoreoPostura
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PostureForegroundService : Service() {

    @Inject
    lateinit var gestorMonitoreo: GestorMonitoreoPostura

    private val CHANNEL_ID = "ErgoHabitCanalPostura"
    private val ALERT_CHANNEL_ID = "ErgoHabitCanalAlertaPostura"
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var ultimaPosturaCorrecta = true
    private var ultimaNotificacionAlerta = 0L

    private val estadoDispositivoReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_SCREEN_OFF -> gestorMonitoreo.pausar()
                Intent.ACTION_SCREEN_ON, Intent.ACTION_USER_PRESENT -> verificarEstadoYReanudar()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        crearCanalesNotificacion()
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_USER_PRESENT)
        }
        registerReceiver(estadoDispositivoReceiver, filter)

        // Escuchar cambios de estado para actualizar la notificación y lanzar alertas
        serviceScope.launch {
            gestorMonitoreo.estado.collectLatest { estado ->
                actualizarNotificacion(estado)
                
                if (estado.activo && !estado.esCorrecta) {
                    val ahora = System.currentTimeMillis()
                    // Lanzar notificación si acaba de cambiar a incorrecta O si lleva más de 7 segundos mal
                    if (ultimaPosturaCorrecta || ahora - ultimaNotificacionAlerta > 7000) {
                        lanzarNotificacionAlerta(estado.mensaje)
                        ultimaNotificacionAlerta = ahora
                    }
                }
                ultimaPosturaCorrecta = estado.esCorrecta
            }
        }
    }

    private fun getPendingIntent(): PendingIntent {
        val intent = packageManager.getLaunchIntentForPackage(packageName) ?: Intent()
        return PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun actualizarNotificacion(estado: EstadoMonitoreoPostura) {
        val titulo = if (estado.activo && !estado.esCorrecta) "¡TU POSTURA ESTÁ MAL!" else "ErgoHabit: Centinela"
        val mensaje = if (estado.activo) {
            if (estado.esCorrecta) "Tu postura es buena. Sigue así."
            else "Atención: ${estado.mensaje}"
        } else {
            "El monitoreo está desactivado"
        }

        val notificacion = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setContentIntent(getPendingIntent())
            .build()

        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(1, notificacion)
    }

    private fun lanzarNotificacionAlerta(mensaje: String) {
        val manager = getSystemService(NotificationManager::class.java)
        
        // Creamos un PendingIntent que abra la app al tocar la notificación
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificacionAlerta = NotificationCompat.Builder(this, ALERT_CHANNEL_ID)
            .setContentTitle("¡URGENTE: CORRIGE TU POSTURA!")
            .setContentText(mensaje)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVibrate(longArrayOf(0, 500, 2000, 500))
            .setFullScreenIntent(pendingIntent, true) // Esto hace que salte en pantalla
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setOnlyAlertOnce(false)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        manager.notify(100, notificacionAlerta)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificacionInicial = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("ErgoHabit: Centinela Postural")
            .setContentText("Iniciando monitoreo...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()

        startForeground(1, notificacionInicial)
        gestorMonitoreo.iniciar()
        verificarEstadoYReanudar()
        return START_STICKY
    }

    private fun verificarEstadoYReanudar() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (powerManager.isInteractive && !keyguardManager.isKeyguardLocked) {
            gestorMonitoreo.reanudar()
        } else {
            gestorMonitoreo.pausar()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(estadoDispositivoReceiver)
        } catch (_: Exception) {
        }
        gestorMonitoreo.detener()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun crearCanalesNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            
            // Canal para el servicio persistente (IMPORTANCIA BAJA para no molestar)
            val canalServicio = NotificationChannel(
                CHANNEL_ID,
                "Servicio de Monitoreo",
                NotificationManager.IMPORTANCE_LOW
            )
            manager.createNotificationChannel(canalServicio)

            // Canal para las alertas críticas (IMPORTANCIA ALTA para que salten en pantalla)
            val canalAlerta = NotificationChannel(
                ALERT_CHANNEL_ID,
                "Alertas de Postura Críticas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones visuales cuando tu postura es incorrecta"
                enableVibration(true)
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), audioAttributes)
            }
            manager.createNotificationChannel(canalAlerta)
        }
    }
}
