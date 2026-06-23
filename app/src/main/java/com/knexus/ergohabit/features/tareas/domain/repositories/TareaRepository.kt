package com.knexus.ergohabit.features.tareas.domain.repositories

import com.knexus.ergohabit.features.tareas.domain.entities.TareaEnfoque
import com.knexus.ergohabit.features.tareas.domain.entities.TareasEstado
import kotlinx.coroutines.flow.Flow

interface TareaRepository {
    fun getTareas(): Flow<Result<TareasEstado>>
    suspend fun createTarea(titulo: String, categoria: String, duracionMinutos: Int): Result<TareaEnfoque>
    suspend fun completarTarea(idTarea: Int): Result<TareaEnfoque>
    suspend fun getMensajeExito(): Result<String>
}
