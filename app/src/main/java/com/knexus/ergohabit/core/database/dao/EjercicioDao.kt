package com.knexus.ergohabit.core.database.dao

import androidx.room.*
import com.knexus.ergohabit.core.database.entities.EjercicioEntity
import com.knexus.ergohabit.core.database.entities.EjercicioSesionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EjercicioDao {
    @Query("SELECT * FROM ejercicio_dashboard WHERE id = 1")
    fun getDashboard(): Flow<EjercicioEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDashboard(dashboard: EjercicioEntity)

    @Query("DELETE FROM ejercicio_dashboard")
    suspend fun clearDashboard()

    // --- SESIÓN ACTUAL ---
    @Query("SELECT * FROM ejercicio_sesion_actual WHERE id = 1")
    suspend fun getSesionActual(): EjercicioSesionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSesionActual(sesion: EjercicioSesionEntity)

    @Query("DELETE FROM ejercicio_sesion_actual")
    suspend fun clearSesionActual()
}
