package com.knexus.ergohabit.features.posture.data.repository

import com.knexus.ergohabit.core.database.dao.NutricionDao
import com.knexus.ergohabit.core.database.entities.NutricionEntity
import com.knexus.ergohabit.features.posture.data.datasource.api.HabitosApi
import com.knexus.ergohabit.features.posture.data.models.ConfigurarNutricionRequest
import com.knexus.ergohabit.features.posture.data.models.MarcarComidaRequest
import com.knexus.ergohabit.features.posture.data.models.NutricionDashboardResponse
import com.knexus.ergohabit.features.posture.domain.repository.NutricionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NutricionRepositoryImpl @Inject constructor(
    private val api: HabitosApi,
    private val dao: NutricionDao
) : NutricionRepository {

    override fun getNutricionDashboard(): Flow<Result<NutricionDashboardResponse>> = flow {
        val localFlow = dao.getNutricionConfig().map { entity ->
            if (entity != null) {
                Result.success(NutricionDashboardResponse(
                    comidasCompletadasText = "",
                    porcentajeCumplimiento = 0,
                    mensajeFaltanteText = "",
                    horaDesayunoConfigurada = entity.horaDesayuno,
                    horaComidaConfigurada = entity.horaComida,
                    horaCenaConfigurada = entity.horaCena,
                    chequeoDesayuno = entity.chequeoDesayuno,
                    chequeoComida = entity.chequeoComida,
                    chequeoCena = entity.chequeoCena
                ))
            } else null
        }

        try {
            val response = api.obtenerDashboardNutricion()
            dao.insertNutricionConfig(NutricionEntity(
                horaDesayuno = response.horaDesayunoConfigurada,
                horaComida = response.horaComidaConfigurada,
                horaCena = response.horaCenaConfigurada,
                chequeoDesayuno = response.chequeoDesayuno,
                chequeoComida = response.chequeoComida,
                chequeoCena = response.chequeoCena
            ))
            emit(Result.success(response))
        } catch (e: Exception) {
            emitAll(localFlow.map { it ?: Result.failure(e) })
        }
    }

    override suspend fun configurarHorarios(desayuno: String, comida: String, cena: String): Result<String> {
        return try {
            val response = api.configurarHorariosNutricion(ConfigurarNutricionRequest(desayuno, comida, cena))
            Result.success(response.mensaje)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun marcarComida(tipo: String, estado: Boolean): Result<String> {
        return try {
            val response = api.marcarComida(MarcarComidaRequest(tipo, estado))
            Result.success(response.mensaje)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
