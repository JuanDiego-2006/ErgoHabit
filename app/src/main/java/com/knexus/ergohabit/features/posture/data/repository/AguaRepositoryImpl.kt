package com.knexus.ergohabit.features.posture.data.repository

import com.knexus.ergohabit.features.posture.data.datasource.api.AguaApi
import com.knexus.ergohabit.features.posture.data.mapper.toDomain
import com.knexus.ergohabit.features.posture.data.models.*
import com.knexus.ergohabit.features.posture.domain.entities.DashboardAgua
import com.knexus.ergohabit.features.posture.domain.entities.RegistroSemanalAgua
import com.knexus.ergohabit.features.posture.domain.repository.AguaRepository
import javax.inject.Inject

class AguaRepositoryImpl @Inject constructor(
    private val api: AguaApi
) : AguaRepository {

    override suspend fun getDashboardAgua(): Result<DashboardAgua> {
        return try {
            val response = api.obtenerDashboardAgua()
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registrarToma(cantidadMl: Int): Result<String> {
        return try {
            val response = api.registrarToma(RegistrarTomaRequest(cantidadMl))
            Result.success(response.mensaje)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun configurarMetaManual(metaMl: Int): Result<String> {
        return try {
            val response = api.configurarMeta(ConfigurarMetaRequest(tipo = "MANUAL", metaMl = metaMl))
            Result.success(response.mensaje)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun configurarMetaPeso(peso: Double, estatura: Double): Result<String> {
        return try {
            val response = api.configurarMeta(ConfigurarMetaRequest(tipo = "PESO", peso = peso, estatura = estatura))
            Result.success(response.mensaje)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProgresoSemanal(): Result<RegistroSemanalAgua> {
        return try {
            val response = api.obtenerProgresoSemanalAgua()
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
