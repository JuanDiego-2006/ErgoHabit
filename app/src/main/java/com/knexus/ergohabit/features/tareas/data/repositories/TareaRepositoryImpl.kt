package com.knexus.ergohabit.features.tareas.data.repositories

import com.google.gson.Gson
import com.knexus.ergohabit.core.database.dao.TareaDao
import com.knexus.ergohabit.core.database.dao.TareaProgresoDao
import com.knexus.ergohabit.core.database.entities.TareaProgresoEntity
import com.knexus.ergohabit.features.tareas.data.datasource.api.TareaApi
import com.knexus.ergohabit.features.tareas.data.mapper.toDomain
import com.knexus.ergohabit.features.tareas.data.mapper.toEntity
import com.knexus.ergohabit.features.tareas.data.models.MessageResponseDto
import com.knexus.ergohabit.features.tareas.data.models.TareaCreateRequestDto
import com.knexus.ergohabit.features.tareas.domain.entities.TareaEnfoque
import com.knexus.ergohabit.features.tareas.domain.entities.TareasEstado
import com.knexus.ergohabit.features.tareas.domain.repositories.TareaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class TareaRepositoryImpl @Inject constructor(
    private val api: TareaApi,
    private val daoProgreso: TareaProgresoDao,
    private val daoTarea: TareaDao
) : TareaRepository {

    override fun getTareas(): Flow<Result<TareasEstado>> = flow {
        // 1. Emitir datos locales primero
        val pendientesLocales = daoTarea.getTareasPendientes().first().map { it.toDomain() }
        val completadasLocales = daoTarea.getTareasCompletadas().first().map { it.toDomain() }
        
        if (pendientesLocales.isNotEmpty() || completadasLocales.isNotEmpty()) {
            emit(Result.success(TareasEstado(
                pendientes = pendientesLocales,
                completadas = completadasLocales,
                totalPendientesText = "PENDIENTES · ${pendientesLocales.size}",
                totalCompletadasText = "COMPLETADAS · ${completadasLocales.size}"
            )))
        }

        try {
            // 2. Fetch de la API
            val response = api.getTareas()
            val estado = response.toDomain()
            
            // 3. Guardar en Room
            val todasLasEntidades = estado.pendientes.map { it.toEntity(esCompletada = false) } +
                                    estado.completadas.map { it.toEntity(esCompletada = true) }
            
            daoTarea.clearTareas()
            daoTarea.insertTareas(todasLasEntidades)
            
            emit(Result.success(estado))
        } catch (e: Exception) {
            // Si falla la API y no había datos locales, emitir el error
            if (pendientesLocales.isEmpty() && completadasLocales.isEmpty()) {
                emit(Result.failure(parseError(e)))
            }
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
            // Actualización optimista local compatible con API 24+
            val now = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US).apply {
                timeZone = java.util.TimeZone.getTimeZone("UTC")
            }.format(java.util.Date())
            
            daoTarea.updateTareaEstado(idTarea, 3, now)
            
            val response = api.iniciarTarea(idTarea)
            Result.success(response.mensaje ?: "Cronómetro iniciado")
        } catch (e: Exception) {
            Result.failure(parseError(e))
        }
    }

    override suspend fun pausarTarea(idTarea: Int): Result<String> {
        return try {
            // Actualización optimista local
            daoTarea.updateTareaEstado(idTarea, 1, null)
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
        daoProgreso.insertProgreso(entity)
    }

    override suspend fun getLocalProgress(idTarea: Int): TareaProgresoEntity? {
        return daoProgreso.getProgresoTarea(idTarea)
    }

    override suspend fun clearLocalProgress(idTarea: Int) {
        daoProgreso.deleteProgreso(idTarea)
    }
}
