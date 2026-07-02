package com.knexus.ergohabit.core.database.dao

import androidx.room.*
import com.knexus.ergohabit.core.database.entities.TareaProgresoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaProgresoDao {
    @Query("SELECT * FROM tareas_progreso WHERE idTarea = :idTarea")
    suspend fun getProgresoTarea(idTarea: Int): TareaProgresoEntity?

    @Query("SELECT * FROM tareas_progreso WHERE idTarea = :idTarea")
    fun getProgresoTareaFlow(idTarea: Int): Flow<TareaProgresoEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgreso(progreso: TareaProgresoEntity)

    @Query("DELETE FROM tareas_progreso WHERE idTarea = :idTarea")
    suspend fun deleteProgreso(idTarea: Int)

    @Query("DELETE FROM tareas_progreso")
    suspend fun clearAll()
}
