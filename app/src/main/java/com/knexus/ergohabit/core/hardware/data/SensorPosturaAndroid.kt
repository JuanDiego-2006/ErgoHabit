package com.knexus.ergohabit.core.hardware.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.knexus.ergohabit.core.hardware.domain.SensorPostura
import com.knexus.ergohabit.core.hardware.domain.model.DatosSensor
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.sqrt

class SensorPosturaAndroid @Inject constructor(
    @ApplicationContext private val contexto: Context
) : SensorPostura {
    private val gestorSensores =
        contexto.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val acelerometro = gestorSensores.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val giroscopio   = gestorSensores.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    override fun iniciarMonitoreo(): Flow<DatosSensor> = callbackFlow {
        var ax = 0f; var ay = 0f; var az = 9.81f
        var gx = 0f; var gy = 0f; var gz = 0f

        var pitchFiltrado = 0.0
        var rollFiltrado  = 0.0
        var ultimoTimestamp = 0L

        // ¡Magia aquí! Bajamos de 0.98 a 0.85 para que reaccione mucho más rápido a tus movimientos
        val alpha = 0.85

        val oyente = object : SensorEventListener {
            override fun onSensorChanged(evento: SensorEvent?) {
                evento ?: return
                when (evento.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        ax = evento.values[0]
                        ay = evento.values[1]
                        az = evento.values[2]

                        val pitchAcel = Math.toDegrees(
                            atan2(ay.toDouble(), sqrt((ax * ax + az * az).toDouble()))
                        )
                        val rollAcel = Math.toDegrees(
                            atan2(-ax.toDouble(), az.toDouble())
                        )

                        if (ultimoTimestamp == 0L) {
                            pitchFiltrado = pitchAcel
                            rollFiltrado  = rollAcel
                            ultimoTimestamp = evento.timestamp
                            return
                        }

                        val dt = (evento.timestamp - ultimoTimestamp) / 1_000_000_000.0
                        ultimoTimestamp = evento.timestamp

                        pitchFiltrado = alpha * (pitchFiltrado + gy * dt) + (1 - alpha) * pitchAcel
                        rollFiltrado  = alpha * (rollFiltrado  + gx * dt) + (1 - alpha) * rollAcel

                        trySend(
                            DatosSensor(
                                inclinacion = pitchFiltrado,
                                balanceo    = rollFiltrado,
                                guinada     = gz.toDouble()
                            )
                        )
                    }
                    Sensor.TYPE_GYROSCOPE -> {
                        gx = Math.toDegrees(evento.values[0].toDouble()).toFloat()
                        gy = Math.toDegrees(evento.values[1].toDouble()).toFloat()
                        gz = Math.toDegrees(evento.values[2].toDouble()).toFloat()
                    }
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, precision: Int) {}
        }

        // Cambiamos a SENSOR_DELAY_GAME para que el envío de datos sea más veloz
        gestorSensores.registerListener(oyente, acelerometro, SensorManager.SENSOR_DELAY_GAME)
        if (giroscopio != null) {
            gestorSensores.registerListener(oyente, giroscopio, SensorManager.SENSOR_DELAY_GAME)
        }
        awaitClose { gestorSensores.unregisterListener(oyente) }
    }
    override fun detenerMonitoreo() { }
}