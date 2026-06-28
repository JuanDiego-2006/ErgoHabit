package com.knexus.ergohabit.features.posture.presentation.services

import android.app.KeyguardManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.knexus.ergohabit.features.posture.domain.GestorMonitoreoPostura
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PostureForegroundService : Service() {

    @Inject
    lateinit var gestorMonitoreo: GestorMonitoreoPostura

    private val CHANNEL_ID = "ErgoHabitCanalPostura"

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
        crearCanalNotificacion()
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_USER_PRESENT)
        }
        registerReceiver(estadoDispositivoReceiver, filter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificacion = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Centinela Ergonómico")
            .setContentText("Protegiendo tu postura con sensores y cámara...")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()

        startForeground(1, notificacion)
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

    private fun crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CHANNEL_ID,
                "Monitoreo de Postura",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(canal)
        }
    }
}
