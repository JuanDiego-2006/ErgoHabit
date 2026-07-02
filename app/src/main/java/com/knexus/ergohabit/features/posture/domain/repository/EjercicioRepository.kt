package com.knexus.ergohabit.features.posture.domain.repository

import com.knexus.ergohabit.features.posture.domain.entities.DashboardEjercicio
import kotlinx.coroutines.flow.Flow

interface EjercicioRepository {
    fun getDashboardEjercicio(): Flow<Result<DashboardEjercicio>>
    suspend fun registrarKilometros(km: Double): Result<String>
    suspend fun configurarMeta(nuevaMeta: Double): Result<String>
    
    // Gestión de sesión local persistente
    suspend fun guardarPasosTemporales(pasos: Int, km: Double)
    suspend fun obtenerPasosTemporales(): Pair<Int, Double>
    suspend fun limpiarPasosTemporales()
}
