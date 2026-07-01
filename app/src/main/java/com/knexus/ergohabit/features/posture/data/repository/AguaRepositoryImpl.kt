package com.knexus.ergohabit.features.posture.data.repository

import com.knexus.ergohabit.core.database.dao.AguaDao
import com.knexus.ergohabit.features.posture.data.datasource.api.AguaApi
import com.knexus.ergohabit.features.posture.data.mapper.*
import com.knexus.ergohabit.features.posture.data.models.*
import com.knexus.ergohabit.features.posture.domain.entities.DashboardAgua
import com.knexus.ergohabit.features.posture.domain.entities.RegistroSemanalAgua
import com.knexus.ergohabit.features.posture.domain.repository.AguaRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class AguaRepositoryImpl @Inject constructor(
    private val api: AguaApi,
    private val dao: AguaDao
) : AguaRepository {

    override fun getDashboardAgua(): Flow<Result<DashboardAgua>> = channelFlow {
        // 1. Iniciar observación de Room
        launch {
            dao.getDashboard().collect { local ->
                if (local != null) {
                    send(Result.success(local.toDomainDashboard()))
                }
            }
        }

        // 2. Actualizar desde la API
        try {
            val response = api.obtenerDashboardAgua()
            dao.insertDashboard(response.toDomain().toEntity())
        } catch (e: Exception) {
            val current = dao.getDashboard().first()
            if (current == null) send(Result.failure(e))
        }
    }

    override suspend fun registrarToma(cantidadMl: Int): Result<String> {
        return try {
            // Actualización optimista local
            val local = dao.getDashboard().first()
            if (local != null) {
                val nuevoConsumo = local.consumidoHoyMl + cantidadMl
                val meta = local.metaDiariaMl.coerceAtLeast(1)
                val nuevosRestantes = (meta - nuevoConsumo).coerceAtLeast(0)
                val nuevoPct = ((nuevoConsumo.toDouble() / meta) * 100).toInt().coerceIn(0, 100)
                dao.updateConsumo(nuevoConsumo, nuevoPct, local.vasosConsumidos + 1, nuevosRestantes)
            }

            val response = api.registrarToma(AguaRegistrarTomaRequest(cantidadMl))
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
