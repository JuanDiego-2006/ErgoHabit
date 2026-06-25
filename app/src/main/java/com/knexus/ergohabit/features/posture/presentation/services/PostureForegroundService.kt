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
import com.knexus.ergohabit.core.hardware.domain.GestorSonido
import com.knexus.ergohabit.features.posture.domain.usecase.PosturaUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class PostureForegroundService : Service() {

    @Inject
    lateinit var posturaUseCase: PosturaUseCase

    @Inject
    lateinit var gestorSonido: GestorSonido

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // Variable para controlar el trabajo de los sensores
    private var monitoreoJob: Job? = null

    private val CHANNEL_ID = "ErgoHabitCanalPostura"
    private var vibrandoAnteriormente = false

    // 1. EL "ESCUCHADOR" DEL ESTADO DEL TELÉFONO
    private val estadoDispositivoReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_SCREEN_OFF -> {
                    // Pantalla apagada -> Matar sensores y apagar sonido
                    pausarMonitoreo()
                }
                Intent.ACTION_SCREEN_ON -> {
                    // Pantalla encendida (pero tal vez bloqueada) -> Verificamos
                    verificarEstadoYReanudar()
                }
                Intent.ACTION_USER_PRESENT -> {
                    // El usuario acaba de desbloquear el teléfono (PIN/Huella) -> Reanudamos
                    verificarEstadoYReanudar()
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        crearCanalNotificacion()

        // Registramos los eventos que queremos escuchar del celular
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
            .setContentText("Protegiendo tu postura...")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()

        startForeground(1, notificacion)

        // Verificamos si podemos arrancar los sensores inmediatamente
        verificarEstadoYReanudar()

        return START_STICKY
    }

    // 2. LÓGICA DE VERIFICACIÓN ESTRICTA
    private fun verificarEstadoYReanudar() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        val isScreenOn = powerManager.isInteractive
        val isLocked = keyguardManager.isKeyguardLocked

        // Solo encendemos el sensor si la pantalla está ON y NO está bloqueada
        if (isScreenOn && !isLocked) {
            reanudarMonitoreo()
        } else {
            pausarMonitoreo()
        }
    }

    // 3. FUNCIÓN PARA PRENDER SENSORES
    private fun reanudarMonitoreo() {
        if (monitoreoJob?.isActive == true) return

        monitoreoJob = serviceScope.launch {
            posturaUseCase().collect { entidad ->

                // DOBLE CANDADO: Si por algún motivo el sensor capta movimiento
                // mientras la pantalla está apagada o bloqueada, lo abortamos de inmediato.
                val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
                val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

                if (!powerManager.isInteractive || keyguardManager.isKeyguardLocked) {
                    pausarMonitoreo()
                    return@collect
                }

                val esIncorrectaAhora = !entidad.esCorrecta

                if (esIncorrectaAhora) {
                    gestorSonido.sonarAlerta()
                } else if (vibrandoAnteriormente && !esIncorrectaAhora) {
                    gestorSonido.detenerSonido()
                }

                vibrandoAnteriormente = esIncorrectaAhora
            }
        }
    }

    // 4. FUNCIÓN PARA APAGAR SENSORES (Ahorro de batería real)
    private fun pausarMonitoreo() {
        monitoreoJob?.cancel() // Apaga el flujo de datos del acelerómetro
        monitoreoJob = null
        gestorSonido.detenerSonido() // Silencia cualquier vibración atascada
        vibrandoAnteriormente = false
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(estadoDispositivoReceiver)
        } catch (e: Exception) {
            // Previene crasheos si se intenta des-registrar algo no registrado
        }
        serviceScope.cancel()
        gestorSonido.detenerSonido()
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