package com.knexus.ergohabit.core.hardware.domain

import com.knexus.ergohabit.core.hardware.domain.model.DatosPasos
import kotlinx.coroutines.flow.Flow

interface SensorEjercicio {
    fun estaDisponible(): Boolean
    fun iniciarMonitoreo(): Flow<DatosPasos>
    fun detenerMonitoreo()
}
