package com.knexus.ergohabit.features.posture.presentation.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.knexus.ergohabit.features.posture.domain.usecase.PosturaUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * Servicio de primer plano (Foreground Service) para el monitoreo continuo de la postura.
 * En la arquitectura, actúa como un "Adaptador de Sistema" en la capa de presentación.
 * Permite que los sensores sigan funcionando incluso cuando la app está en segundo plano.
 */
@AndroidEntryPoint
class PostureForegroundService : Service() {

    @Inject
    lateinit var posturaUseCase: PosturaUseCase

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val CHANNEL_ID = "ErgoHabitCanalPostura"

    override fun onCreate() {
        super.onCreate()
        crearCanalNotificacion()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificacion = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Centinela Ergonómico")
            .setContentText("Monitoreando tu postura en tiempo real...")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()

        startForeground(1, notificacion)
        
        // Iniciar la lógica de monitoreo desde el Caso de Uso
        iniciarMonitoreo()

        return START_STICKY
    }

    private fun iniciarMonitoreo() {
        serviceScope.launch {
            posturaUseCase().collect { entidad ->
                // Aquí el servicio podría actualizar la notificación o disparar alertas
                // si la postura es incorrecta durante mucho tiempo.
                if (!entidad.esCorrecta) {
                    println("Servicio: Postura incorrecta detectada.")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
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
