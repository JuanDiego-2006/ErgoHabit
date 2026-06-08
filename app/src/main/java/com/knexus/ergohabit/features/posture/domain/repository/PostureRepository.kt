package com.knexus.ergohabit.features.posture.domain.repository

import com.knexus.ergohabit.features.posture.domain.entities.EntidadPostura
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz del repositorio de postura.
 */
interface PostureRepository {
    fun getPostureData(): Flow<EntidadPostura>
    
    /**
     * Simula o ejecuta el envío de datos al servidor.
     */
    suspend fun enviarReportePostura(entidad: EntidadPostura): Result<Boolean>
}
