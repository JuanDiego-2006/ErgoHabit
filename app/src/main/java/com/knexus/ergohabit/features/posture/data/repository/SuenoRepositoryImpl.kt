package com.knexus.ergohabit.features.posture.data.repository

import com.google.gson.Gson
import com.knexus.ergohabit.core.database.dao.SuenoDao
import com.knexus.ergohabit.core.database.entities.SuenoEntity
import com.knexus.ergohabit.features.posture.data.datasource.api.HabitosApi
import com.knexus.ergohabit.features.posture.data.models.ErrorResponseDto
import com.knexus.ergohabit.features.posture.data.models.SuenoRequest
import com.knexus.ergohabit.features.posture.data.models.SuenoResponse
import com.knexus.ergohabit.features.posture.domain.repository.SuenoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import javax.inject.Inject

class SuenoRepositoryImpl @Inject constructor(
    private val api: HabitosApi,
    private val dao: SuenoDao
) : SuenoRepository {

    private val gson = Gson()

    override fun getSuenoDashboard(): Flow<Result<SuenoResponse>> = flow {
        val localFlow = dao.getSuenoDashboard().map { entity ->
            if (entity != null) {
                Result.success(SuenoResponse(
                    horasPlanificadas = entity.horasPlanificadas,
                    horasDormidasReales = entity.horasDormidasReales,
                    despertoATiempo = entity.despertoATiempo,
                    horaDormirConfigurada = entity.horaDormirConfigurada,
                    horaDespertarConfigurada = entity.horaDespertarConfigurada,
                    porcentajeCumplimiento = entity.porcentajeCumplimiento
                ))
            } else {
                null
            }
        }

        try {
            val response = api.obtenerDashboardSueno()
            dao.insertSuenoDashboard(SuenoEntity(
                horasPlanificadas = response.horasPlanificadas,
                horasDormidasReales = response.horasDormidasReales,
                despertoATiempo = response.despertoATiempo,
                horaDormirConfigurada = response.horaDormirConfigurada,
                horaDespertarConfigurada = response.horaDespertarConfigurada,
                porcentajeCumplimiento = response.porcentajeCumplimiento,
                fraseMotivacional = response.fraseMotivacional
            ))
            emit(Result.success(response))
        } catch (e: Exception) {
            emitAll(localFlow.map { it ?: Result.failure(e) })
        }
    }

    override suspend fun registrarDespertar(): Result<String> {
        return try {
            val response = api.registrarDespertarSueno()
            Result.success(response.mensaje)
        } catch (e: Exception) {
            parseError(e)
        }
    }

    override suspend fun configurarHorario(horaDespertar: String, horaDormir: String): Result<String> {
        return try {
            val response = api.configurarHorarioSueno(SuenoRequest(horaDespertar, horaDormir))
            Result.success(response.mensaje)
        } catch (e: Exception) {
            parseError(e)
        }
    }

    private fun parseError(e: Exception): Result<String> {
        if (e is HttpException) {
            try {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = gson.fromJson(errorBody, ErrorResponseDto::class.java)
                return Result.failure(Exception(errorResponse.message))
            } catch (_: Exception) {}
        }
        return Result.failure(e)
    }
}
