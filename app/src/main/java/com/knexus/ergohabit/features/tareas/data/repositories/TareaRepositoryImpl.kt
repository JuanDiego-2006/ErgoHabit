package com.knexus.ergohabit.features.tareas.data.repositories

import com.google.gson.Gson
import com.knexus.ergohabit.core.database.dao.TareaProgresoDao
import com.knexus.ergohabit.core.database.entities.TareaProgresoEntity
import com.knexus.ergohabit.features.tareas.data.datasource.api.TareaApi
import com.knexus.ergohabit.features.tareas.data.mapper.toDomain
import com.knexus.ergohabit.features.tareas.data.models.MessageResponseDto
import com.knexus.ergohabit.features.tareas.data.models.TareaCreateRequestDto
import com.knexus.ergohabit.features.tareas.domain.entities.TareaEnfoque
import com.knexus.ergohabit.features.tareas.domain.entities.TareasEstado
import com.knexus.ergohabit.features.tareas.domain.repositories.TareaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class TareaRepositoryImpl @Inject constructor(
    private val api: TareaApi,
    private val dao: TareaProgresoDao
) : TareaRepository {

    override fun getTareas(): Flow<Result<TareasEstado>> = flow {
        try {
            val response = api.getTareas()
            emit(Result.success(response.toDomain()))
        } catch (e: Exception) {
            emit(Result.failure(parseError(e)))
        }
    }

    override suspend fun createTarea(titulo: String, categoria: String, duracionMinutos: Int): Result<String> {
        return try {
            val request = TareaCreateRequestDto(titulo, categoria, duracionMinutos)
            val response = api.createTarea(request)
            Result.success(response.mensaje ?: "Tarea creada")
        } catch (e: Exception) {
            Result.failure(parseError(e))
        }
    }

    override suspend fun iniciarTarea(idTarea: Int): Result<String> {
        return try {
            val response = api.iniciarTarea(idTarea)
            Result.success(response.mensaje ?: "Cronómetro iniciado")
        } catch (e: Exception) {
            Result.failure(parseError(e))
        }
    }

    override suspend fun pausarTarea(idTarea: Int): Result<String> {
        return try {
            val response = api.pausarTarea(idTarea)
            Result.success(response.mensaje ?: "Cronómetro pausado")
        } catch (e: Exception) {
            Result.failure(parseError(e))
        }
    }

    override suspend fun completarTarea(idTarea: Int): Result<String> {
        return try {
            val response = api.completarTarea(idTarea)
            Result.success(response.mensaje ?: "Tarea completada")
        } catch (e: Exception) {
            Result.failure(parseError(e))
        }
    }

    override suspend fun extenderTarea(idTarea: Int, minutos: Int): Result<String> {
        return try {
            val request = com.knexus.ergohabit.features.tareas.data.models.TareaExtenderRequestDto(minutos)
            val response = api.extenderTarea(idTarea, request)
            Result.success(response.mensaje ?: "Tiempo extendido")
        } catch (e: Exception) {
            Result.failure(parseError(e))
        }
    }

    override suspend fun eliminarTarea(idTarea: Int): Result<String> {
        return try {
            val response = api.eliminarTarea(idTarea)
            Result.success(response.mensaje ?: "Tarea eliminada")
        } catch (e: Exception) {
            Result.failure(parseError(e))
        }
    }

    override suspend fun getInfoCronometro(idTarea: Int): Result<com.knexus.ergohabit.features.tareas.domain.entities.AlertaSalud> {
        return try {
            val response = api.getInfoCronometro(idTarea)
            Result.success(
                com.knexus.ergohabit.features.tareas.domain.entities.AlertaSalud(
                    frase = response.fraseMotivacional,
                    accion = response.accionFisica,
                    requierePostura = response.requiereAlertasPostura
                )
            )
        } catch (e: Exception) {
            Result.failure(parseError(e))
        }
    }


    private fun parseError(e: Exception): Exception {
        if (e is HttpException) {
            return try {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, MessageResponseDto::class.java)
                Exception(errorResponse.mensaje ?: errorResponse.errorMensaje ?: e.message())
            } catch (ex: Exception) {
                e
            }
        }
        return e
    }

    override suspend fun saveLocalProgress(idTarea: Int, restante: Int, inicial: Int, endTime: Long) {
        val entity = TareaProgresoEntity(idTarea, restante, inicial, endTime)
        dao.insertProgreso(entity)
    }

    override suspend fun getLocalProgress(idTarea: Int): TareaProgresoEntity? {
        return dao.getProgresoTarea(idTarea)
    }

    override suspend fun clearLocalProgress(idTarea: Int) {
        dao.deleteProgreso(idTarea)
    }
}
