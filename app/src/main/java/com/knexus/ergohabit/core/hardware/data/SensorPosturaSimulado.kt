package com.knexus.ergohabit.core.hardware.data

import com.knexus.ergohabit.core.hardware.domain.SensorPostura
import com.knexus.ergohabit.core.hardware.domain.model.DatosSensor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.math.sin

/**
 * Simulador de sensor que genera datos de inclinación automáticos para pruebas.
 */
class SensorPosturaSimulado @Inject constructor() : SensorPostura {

    override fun iniciarMonitoreo(): Flow<DatosSensor> = flow {
        var tiempo = 0.0
        while (true) {
            // Generamos una onda senoidal para que los grados suban y bajen suavemente
            // Oscilará entre -30 y 30 grados aproximadamente
            val inclinacionSimulada = sin(tiempo) * 25.0
            val balanceoSimulado = sin(tiempo * 0.5) * 10.0
            
            emit(DatosSensor(
                inclinacion = inclinacionSimulada,
                balanceo = balanceoSimulado
            ))
            
            tiempo += 0.2
            delay(500) // Emitir nuevos datos cada medio segundo
        }
    }

    override fun detenerMonitoreo() {
        // El flujo se detiene automáticamente cuando se cancela el Scope
    }
}
