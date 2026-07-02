package com.knexus.ergohabit.features.posture.data.repository

import com.knexus.ergohabit.core.database.dao.NutricionDao
import com.knexus.ergohabit.core.database.entities.NutricionEntity
import com.knexus.ergohabit.core.session.SessionManager
import com.knexus.ergohabit.features.posture.data.datasource.api.HabitosApi
import com.knexus.ergohabit.features.posture.data.models.ConfigurarNutricionRequest
import com.knexus.ergohabit.features.posture.data.models.MarcarComidaRequest
import com.knexus.ergohabit.features.posture.data.models.NutricionDashboardResponse
import com.knexus.ergohabit.features.posture.data.models.ErrorResponseDto
import com.knexus.ergohabit.features.posture.domain.repository.NutricionRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

class NutricionRepositoryImpl @Inject constructor(
    private val api: HabitosApi,
    private val dao: NutricionDao,
    private val sessionManager: SessionManager
) : NutricionRepository {

    private val gson = Gson()

    override fun getNutricionDashboard(): Flow<Result<NutricionDashboardResponse>> = channelFlow {
        // 1. OBSERVACIÓN INFINITA: Sincronización inmediata con Room
        launch {
            dao.getNutricionConfig().collect { local ->
                if (local != null) {
                    val completadas = listOf(local.chequeoDesayuno, local.chequeoComida, local.chequeoCena).count { it }
                    val pct = ((completadas / 3.0) * 100).toInt()
                    
                    send(Result.success(NutricionDashboardResponse(
                        comidasCompletadasText = "$completadas / 3 comidas", 
                        porcentajeCumplimiento = pct,
                        mensajeFaltanteText = if (completadas == 3) "¡Meta cumplida!" else "Te faltan ${3 - completadas} comidas",
                        horaDesayunoConfigurada = local.horaDesayuno,
                        horaComidaConfigurada = local.horaComida,
                        horaCenaConfigurada = local.horaCena,
                        chequeoDesayuno = local.chequeoDesayuno,
                        chequeoComida = local.chequeoComida,
                        chequeoCena = local.chequeoCena
                    )))
                }
            }
        }

        // 2. ACTUALIZACIÓN DE RED
        try {
            val response = api.obtenerDashboardNutricion()
            val userId = sessionManager.fetchUserId()
            if (userId != -1) {
                dao.insertNutricionConfig(NutricionEntity(
                    userId = userId,
                    horaDesayuno = response.horaDesayunoConfigurada,
                    horaComida = response.horaComidaConfigurada,
                    horaCena = response.horaCenaConfigurada,
                    chequeoDesayuno = response.chequeoDesayuno,
                    chequeoComida = response.chequeoComida,
                    chequeoCena = response.chequeoCena
                ))
            }
        } catch (_: Exception) {}
    }

    override suspend fun configurarHorarios(desayuno: String, comida: String, cena: String): Result<String> {
        return try {
            val response = api.configurarHorariosNutricion(ConfigurarNutricionRequest(desayuno, comida, cena))
            val userId = sessionManager.fetchUserId()
            if (userId != -1) {
                dao.insertNutricionConfig(NutricionEntity(
                    userId = userId,
                    horaDesayuno = desayuno,
                    horaComida = comida,
                    horaCena = cena
                ))
            }
            Result.success(response.mensaje)
        } catch (e: Exception) {
            parseError(e)
        }
    }

    override suspend fun marcarComida(tipo: String, estado: Boolean): Result<String> {
        return try {
            val response = api.marcarComida(MarcarComidaRequest(tipo, estado))
            val current = dao.getNutricionConfig().firstOrNull()
            if (current != null) {
                dao.insertNutricionConfig(current.copy(
                    chequeoDesayuno = if (tipo == "DESAYUNO") estado else current.chequeoDesayuno,
                    chequeoComida = if (tipo == "COMIDA") estado else current.chequeoComida,
                    chequeoCena = if (tipo == "CENA") estado else current.chequeoCena
                ))
            }
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
