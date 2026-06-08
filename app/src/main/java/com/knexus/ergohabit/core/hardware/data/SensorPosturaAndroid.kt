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

/**
 * Implementación de Android para el sensor de postura utilizando el acelerómetro y el giroscopio.
 */
class SensorPosturaAndroid @Inject constructor(
    @ApplicationContext private val contexto: Context
) : SensorPostura {

    private val gestorSensores = contexto.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val acelerometro = gestorSensores.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val giroscopio = gestorSensores.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    override fun iniciarMonitoreo(): Flow<DatosSensor> = callbackFlow {
        val oyente = object : SensorEventListener {
            override fun onSensorChanged(evento: SensorEvent?) {
                if (evento?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                    val x = evento.values[0]
                    val y = evento.values[1]
                    val z = evento.values[2]

                    // Cálculo de inclinación y balanceo en grados
                    val inclinacion = Math.toDegrees(atan2(y.toDouble(), sqrt((x * x + z * z).toDouble())))
                    val balanceo = Math.toDegrees(atan2(-x.toDouble(), z.toDouble()))

                    // Enviamos los datos procesados con los nuevos nombres en español
                    trySend(DatosSensor(inclinacion = inclinacion, balanceo = balanceo))
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, precision: Int) {}
        }

        // Registrar los oyentes para los sensores
        gestorSensores.registerListener(oyente, acelerometro, SensorManager.SENSOR_DELAY_UI)
        gestorSensores.registerListener(oyente, giroscopio, SensorManager.SENSOR_DELAY_UI)

        awaitClose {
            gestorSensores.unregisterListener(oyente)
        }
    }

    override fun detenerMonitoreo() {
        // Manejado por awaitClose
    }
}
