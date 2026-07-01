package com.knexus.ergohabit.features.posture.data.repository

import com.knexus.ergohabit.core.database.dao.SuenoDao
import com.knexus.ergohabit.core.database.entities.SuenoEntity
import com.knexus.ergohabit.features.posture.data.datasource.api.HabitosApi
import com.knexus.ergohabit.features.posture.data.models.SuenoRequest
import com.knexus.ergohabit.features.posture.data.models.SuenoResponse
import com.knexus.ergohabit.features.posture.domain.repository.SuenoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SuenoRepositoryImpl @Inject constructor(
    private val api: HabitosApi,
    private val dao: SuenoDao
) : SuenoRepository {

    override fun getSuenoDashboard(): Flow<Result<SuenoResponse>> = flow {
        // 1. Emitir lo que haya en Room primero (Caché Offline)
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

        // 2. Intentar actualizar desde la API
        try {
            val response = api.obtenerDashboardSueno()
            // Guardar en Room para la próxima vez
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
            // Si falla la red, emitir lo que teníamos en Room (si había algo)
            emitAll(localFlow.map { it ?: Result.failure(e) })
        }
    }

    override suspend fun registrarDespertar(): Result<String> {
        return try {
            val response = api.registrarDespertarSueno()
            Result.success(response.mensaje)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun configurarHorario(horaDespertar: String, horaDormir: String): Result<String> {
        return try {
            val response = api.configurarHorarioSueno(SuenoRequest(horaDespertar, horaDormir))
            Result.success(response.mensaje)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
