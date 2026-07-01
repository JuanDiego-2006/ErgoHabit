package com.knexus.ergohabit.features.posture.data.repository

import com.knexus.ergohabit.core.database.dao.NutricionDao
import com.knexus.ergohabit.core.database.entities.NutricionEntity
import com.knexus.ergohabit.features.posture.data.datasource.api.HabitosApi
import com.knexus.ergohabit.features.posture.data.models.ConfigurarNutricionRequest
import com.knexus.ergohabit.features.posture.data.models.MarcarComidaRequest
import com.knexus.ergohabit.features.posture.data.models.NutricionDashboardResponse
import com.knexus.ergohabit.features.posture.data.models.ErrorResponseDto
import com.knexus.ergohabit.features.posture.domain.repository.NutricionRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import javax.inject.Inject

class NutricionRepositoryImpl @Inject constructor(
    private val api: HabitosApi,
    private val dao: NutricionDao
) : NutricionRepository {

    private val gson = Gson()

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
                horaDesayuno = normalizarA24h(response.horaDesayunoConfigurada),
                horaComida = normalizarA24h(response.horaComidaConfigurada),
                horaCena = normalizarA24h(response.horaCenaConfigurada),
                chequeoDesayuno = response.chequeoDesayuno,
                chequeoComida = response.chequeoComida,
                chequeoCena = response.chequeoCena
            ))
            emit(Result.success(response))
        } catch (e: Exception) {
            emitAll(localFlow.map { it ?: Result.failure(e) })
        }
    }

    private fun normalizarA24h(hora: String): String {
        if (hora.isBlank() || hora == "--:--" || hora == "Sin establecer" || hora == "00:00") return ""
        val clean = hora.trim().uppercase()
        if (!clean.contains("AM") && !clean.contains("PM")) {
            return try {
                val partes = clean.split(" ")[0].split(":")
                val h = partes[0].toInt()
                val m = partes.getOrNull(1)?.take(2) ?: "00"
                String.format(java.util.Locale.ROOT, "%02d:%s", h, m)
            } catch (e: Exception) { clean.take(5) }
        }
        return try {
            val partes = clean.split(" ")
            val tiempo = partes[0]
            val ampm = partes.getOrNull(1) ?: ""
            val clockPartes = tiempo.split(":")
            var h = clockPartes[0].toInt()
            val m = clockPartes[1].take(2)
            if (ampm == "PM" && h < 12) h += 12
            if (ampm == "AM" && h == 12) h = 0
            String.format(java.util.Locale.ROOT, "%02d:%s", h, m)
        } catch (e: Exception) { clean.take(5) }
    }

    override suspend fun configurarHorarios(desayuno: String, comida: String, cena: String): Result<String> {
        return try {
            val response = api.configurarHorariosNutricion(ConfigurarNutricionRequest(desayuno, comida, cena))
            
            // Actualización local para que la UI responda instantáneamente
            dao.insertNutricionConfig(NutricionEntity(
                horaDesayuno = desayuno,
                horaComida = comida,
                horaCena = cena
            ))

            Result.success(response.mensaje)
        } catch (e: Exception) {
            parseError(e)
        }
    }

    override suspend fun marcarComida(tipo: String, estado: Boolean): Result<String> {
        return try {
            val response = api.marcarComida(MarcarComidaRequest(tipo, estado))
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
