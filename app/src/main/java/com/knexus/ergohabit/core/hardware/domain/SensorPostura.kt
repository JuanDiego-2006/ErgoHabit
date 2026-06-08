package com.knexus.ergohabit.core.hardware.domain

import com.knexus.ergohabit.core.hardware.domain.model.DatosSensor
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz para el monitoreo de la postura mediante sensores de hardware.
 */
interface SensorPostura {
    fun iniciarMonitoreo(): Flow<DatosSensor>
    fun detenerMonitoreo()
}
