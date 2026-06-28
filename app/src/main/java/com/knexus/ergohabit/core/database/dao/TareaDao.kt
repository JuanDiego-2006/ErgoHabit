package com.knexus.ergohabit.core.database.dao

import androidx.room.*
import com.knexus.ergohabit.core.database.entities.TareaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaDao {
    @Query("SELECT * FROM tareas WHERE esCompletada = 0")
    fun getTareasPendientes(): Flow<List<TareaEntity>>

    @Query("SELECT * FROM tareas WHERE esCompletada = 1")
    fun getTareasCompletadas(): Flow<List<TareaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTareas(tareas: List<TareaEntity>)

    @Query("DELETE FROM tareas WHERE id = :idTarea")
    suspend fun deleteTarea(idTarea: Int)

    @Query("DELETE FROM tareas")
    suspend fun clearTareas()
}
