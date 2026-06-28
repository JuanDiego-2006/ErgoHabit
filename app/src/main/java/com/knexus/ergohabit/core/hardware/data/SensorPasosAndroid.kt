package com.knexus.ergohabit.core.hardware.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.knexus.ergohabit.core.hardware.domain.SensorEjercicio
import com.knexus.ergohabit.core.hardware.domain.model.DatosPasos
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class SensorPasosAndroid @Inject constructor(
    @ApplicationContext private val contexto: Context
) : SensorEjercicio {

    private val gestorSensores =
        contexto.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val contadorPasos = gestorSensores.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private val detectorPasos = gestorSensores.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

    override fun estaDisponible(): Boolean =
        contadorPasos != null || detectorPasos != null

    override fun iniciarMonitoreo(): Flow<DatosPasos> = callbackFlow {
        var pasosBase: Int? = null
        var pasosSesion = 0

        val oyente = object : SensorEventListener {
            override fun onSensorChanged(evento: SensorEvent?) {
                evento ?: return
                when (evento.sensor.type) {
                    Sensor.TYPE_STEP_COUNTER -> {
                        val total = evento.values[0].toInt()
                        if (pasosBase == null) pasosBase = total
                        pasosSesion = total - (pasosBase ?: total)
                        trySend(pasosSesion.toDatosPasos())
                    }
                    Sensor.TYPE_STEP_DETECTOR -> {
                        pasosSesion++
                        trySend(pasosSesion.toDatosPasos())
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, precision: Int) = Unit
        }

        when {
            contadorPasos != null -> {
                gestorSensores.registerListener(
                    oyente,
                    contadorPasos,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            }
            detectorPasos != null -> {
                gestorSensores.registerListener(
                    oyente,
                    detectorPasos,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            }
            else -> {
                close(IllegalStateException("Sensor de pasos no disponible"))
                return@callbackFlow
            }
        }

        awaitClose { gestorSensores.unregisterListener(oyente) }
    }

    override fun detenerMonitoreo() = Unit

    private companion object {
        const val PASOS_POR_KM = 1300.0

        fun Int.toDatosPasos(): DatosPasos {
            val km = this / PASOS_POR_KM
            return DatosPasos(pasos = this, km = km)
        }
    }
}
