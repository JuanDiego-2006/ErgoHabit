package com.knexus.ergohabit.features.posture.domain.repository

import com.knexus.ergohabit.features.posture.domain.entities.EntidadPostura
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz del repositorio de postura.
 */
interface PostureRepository {
    fun getPostureData(): Flow<EntidadPostura>
    
    /**
     * Guarda un reporte de postura incorrecta localmente.
     */
    suspend fun guardarReporteLocal(entidad: EntidadPostura): Result<Unit>

    /**
     * Sincroniza las alertas acumuladas con el servidor.
     */
    suspend fun sincronizarConServidor(): Result<Boolean>
}
