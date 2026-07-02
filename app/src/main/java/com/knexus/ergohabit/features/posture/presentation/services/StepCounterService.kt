package com.knexus.ergohabit.features.posture.presentation.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.knexus.ergohabit.core.hardware.domain.SensorEjercicio
import com.knexus.ergohabit.features.posture.domain.GestorEjercicio
import com.knexus.ergohabit.features.posture.domain.repository.EjercicioRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StepCounterService : Service() {

    @Inject
    lateinit var sensorEjercicio: SensorEjercicio

    @Inject
    lateinit var gestorEjercicio: GestorEjercicio

    @Inject
    lateinit var repository: EjercicioRepository

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var sensorJob: Job? = null
    private var syncJob: Job? = null
    private val CHANNEL_ID = "ErgoHabitCanalEjercicio"
    private val NOTIFICATION_ID = 2

    override fun onCreate() {
        super.onCreate()
        crearCanalNotificacion()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificacion = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Contando tus pasos")
            .setContentText("ErgoHabit está registrando tu actividad física...")
            .setSmallIcon(android.R.drawable.ic_menu_directions)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()

        startForeground(NOTIFICATION_ID, notificacion)
        iniciarMonitoreo()
        iniciarSincronizacionAutomatica()
        return START_STICKY
    }

    private fun iniciarMonitoreo() {
        if (gestorEjercicio.estaCorriendo.value) return

        sensorJob?.cancel()
        sensorJob = serviceScope.launch {
            gestorEjercicio.setCorriendo(true)
            sensorEjercicio.iniciarMonitoreo()
                .catch {
                    gestorEjercicio.setCorriendo(false)
                    stopSelf()
                }
                .collect { datos ->
                    gestorEjercicio.actualizarDatos(datos.pasos, datos.km)
                }
        }
    }

    private fun iniciarSincronizacionAutomatica() {
        syncJob?.cancel()
        syncJob = serviceScope.launch {
            while (true) {
                // Espera 2 horas (2 * 60 min * 60 seg * 1000 ms)
                delay(2 * 60 * 60 * 1000)

                val kmParaEnviar = gestorEjercicio.datosPasos.value.km

                if (kmParaEnviar > 0.05) { // Solo enviamos si caminó al menos 50 metros
                    repository.registrarKilometros(kmParaEnviar).onSuccess {
                        // Si el envío es exitoso, reiniciamos el contador local de la sesión
                        gestorEjercicio.reiniciar()
                    }.onFailure {
                        // Si falla (sin internet), no reiniciamos.
                        // Se acumulará para el siguiente intento en 2 horas.
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorJob?.cancel()
        syncJob?.cancel()
        gestorEjercicio.setCorriendo(false)
        sensorEjercicio.detenerMonitoreo()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CHANNEL_ID,
                "Conteo de Pasos",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(canal)
        }
    }
}
