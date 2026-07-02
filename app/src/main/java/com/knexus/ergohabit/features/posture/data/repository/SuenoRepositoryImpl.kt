package com.knexus.ergohabit.features.posture.data.repository

import com.google.gson.Gson
import com.knexus.ergohabit.core.database.dao.SuenoDao
import com.knexus.ergohabit.core.database.entities.SuenoEntity
import com.knexus.ergohabit.core.session.SessionManager
import com.knexus.ergohabit.features.posture.data.datasource.api.HabitosApi
import com.knexus.ergohabit.features.posture.data.models.ErrorResponseDto
import com.knexus.ergohabit.features.posture.data.models.SuenoRequest
import com.knexus.ergohabit.features.posture.data.models.SuenoResponse
import com.knexus.ergohabit.features.posture.domain.repository.SuenoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

class SuenoRepositoryImpl @Inject constructor(
    private val api: HabitosApi,
    private val dao: SuenoDao,
    private val sessionManager: SessionManager
) : SuenoRepository {

    private val gson = Gson()

    override fun getSuenoDashboard(): Flow<Result<SuenoResponse>> = channelFlow {
        val initialLocal = dao.getSuenoDashboard().firstOrNull()
        if (initialLocal != null) {
            send(Result.success(mapEntityToResponse(initialLocal)))
        }

        launch {
            dao.getSuenoDashboard().collect { local ->
                if (local != null) {
                    send(Result.success(mapEntityToResponse(local)))
                }
            }
        }

        try {
            val response = api.obtenerDashboardSueno()
            val userId = sessionManager.fetchUserId()
            if (userId != -1) {
                val currentLocal = dao.getSuenoDashboard().firstOrNull()
                dao.insertSuenoDashboard(SuenoEntity(
                    userId = userId,
                    horasPlanificadas = response.horasPlanificadas,
                    horasDormidasReales = response.horasDormidasReales,
                    despertoATiempo = response.despertoATiempo,
                    horaDormirConfigurada = response.horaDormirConfigurada,
                    horaDespertarConfigurada = response.horaDespertarConfigurada,
                    porcentajeCumplimiento = response.porcentajeCumplimiento,
                    fraseMotivacional = response.fraseMotivacional,
                    isAlarmActive = currentLocal?.isAlarmActive ?: false,
                    isSoundEnabled = currentLocal?.isSoundEnabled ?: false,
                    notificacionesHabilitadas = currentLocal?.notificacionesHabilitadas ?: true
                ))
            }
        } catch (_: Exception) { }
    }

    private fun mapEntityToResponse(local: SuenoEntity) = SuenoResponse(
        horasPlanificadas = local.horasPlanificadas,
        horasDormidasReales = local.horasDormidasReales,
        despertoATiempo = local.despertoATiempo,
        horaDormirConfigurada = local.horaDormirConfigurada,
        horaDespertarConfigurada = local.horaDespertarConfigurada,
        porcentajeCumplimiento = local.porcentajeCumplimiento,
        fraseMotivacional = local.fraseMotivacional,
        isAlarmActiveLocal = local.isAlarmActive,
        isSoundEnabledLocal = local.isSoundEnabled,
        notificacionesHabilitadasLocal = local.notificacionesHabilitadas
    )

    override suspend fun registrarDespertar(): Result<String> {
        return try {
            val response = api.registrarDespertarSueno()
            dao.updateAlarmStatus(false)
            Result.success(response.mensaje)
        } catch (e: Exception) {
            parseError(e)
        }
    }

    override suspend fun configurarHorario(horaDespertar: String, horaDormir: String): Result<String> {
        return try {
            val response = api.configurarHorarioSueno(SuenoRequest(horaDespertar, horaDormir))
            val current = dao.getSuenoDashboard().firstOrNull()
            val userId = sessionManager.fetchUserId()
            if (current != null && userId != -1) {
                dao.insertSuenoDashboard(current.copy(
                    horaDespertarConfigurada = horaDespertar,
                    horaDormirConfigurada = horaDormir
                ))
            }
            Result.success(response.mensaje)
        } catch (e: Exception) {
            parseError(e)
        }
    }

    override suspend fun setAlertaActiva(activa: Boolean) {
        dao.updateAlarmStatus(activa)
    }

    override suspend fun silenciarAlerta() {
        dao.setSoundStatus(false)
    }

    override suspend fun setNotificacionesHabilitadas(habilitadas: Boolean) {
        dao.setNotificacionesStatus(habilitadas)
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
