package com.knexus.ergohabit.features.posture.domain.repository

import com.knexus.ergohabit.features.posture.domain.entities.DashboardAgua
import com.knexus.ergohabit.features.posture.domain.entities.RegistroSemanalAgua

import kotlinx.coroutines.flow.Flow

interface AguaRepository {
    fun getDashboardAgua(): Flow<Result<DashboardAgua>>
    suspend fun registrarToma(cantidadMl: Int): Result<String>
    suspend fun configurarMetaManual(metaMl: Int): Result<String>
    suspend fun configurarMetaPeso(peso: Double, estatura: Double): Result<String>
    suspend fun getProgresoSemanal(): Result<RegistroSemanalAgua>
}
