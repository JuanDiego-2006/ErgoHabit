package com.knexus.ergohabit.features.tareas.domain.repositories

import com.knexus.ergohabit.features.tareas.domain.entities.TareaEnfoque
import com.knexus.ergohabit.features.tareas.domain.entities.TareasEstado
import kotlinx.coroutines.flow.Flow

interface TareaRepository {
    fun getTareas(): Flow<Result<TareasEstado>>
    suspend fun createTarea(titulo: String, categoria: String, duracionMinutos: Int): Result<String>
    suspend fun iniciarTarea(idTarea: Int): Result<String>
    suspend fun pausarTarea(idTarea: Int): Result<String>
    suspend fun completarTarea(idTarea: Int): Result<String>
    suspend fun extenderTarea(idTarea: Int, minutos: Int): Result<String>
    suspend fun eliminarTarea(idTarea: Int): Result<String>
    suspend fun getInfoCronometro(idTarea: Int): Result<com.knexus.ergohabit.features.tareas.domain.entities.AlertaSalud>



    suspend fun saveLocalProgress(idTarea: Int, restante: Int, inicial: Int, endTime: Long)
    suspend fun getLocalProgress(idTarea: Int): com.knexus.ergohabit.core.database.entities.TareaProgresoEntity?
    suspend fun clearLocalProgress(idTarea: Int)
}
