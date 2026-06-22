package com.knexus.ergohabit.features.tareas.data.repositories

import com.knexus.ergohabit.features.tareas.data.datasource.api.TareaApi
import com.knexus.ergohabit.features.tareas.data.mapper.toDomain
import com.knexus.ergohabit.features.tareas.data.mapper.toDto
import com.knexus.ergohabit.features.tareas.domain.entities.CategoriaTarea
import com.knexus.ergohabit.features.tareas.domain.entities.TareaEnfoque
import com.knexus.ergohabit.features.tareas.domain.repositories.TareaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TareaRepositoryImpl @Inject constructor(
    private val api: TareaApi
) : TareaRepository {
    override fun getTareas(idUsuario: Int): Flow<Result<List<TareaEnfoque>>> = flow {
        try {
            val response = api.getTareas(idUsuario)
            emit(Result.success(response.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun createTarea(tarea: TareaEnfoque): Flow<Result<TareaEnfoque>> = flow {
        try {
            val response = api.createTarea(tarea.toDto())
            emit(Result.success(response.toDomain()))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun completarTarea(idTarea: Int): Flow<Result<TareaEnfoque>> = flow {
        try {
            val response = api.completarTarea(idTarea)
            emit(Result.success(response.toDomain()))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getMensajeExito(): Flow<Result<String>> = flow {
        try {
            val response = api.getMensajeExito()
            emit(Result.success(response["mensaje"] ?: "¡Excelente trabajo!"))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getCategorias(): Flow<Result<List<CategoriaTarea>>> = flow {
        try {
            val response = api.getCategorias()
            emit(Result.success(response.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
