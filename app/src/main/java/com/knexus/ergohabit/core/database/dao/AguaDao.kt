package com.knexus.ergohabit.core.database.dao

import androidx.room.*
import com.knexus.ergohabit.core.database.entities.AguaDashboardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AguaDao {
    @Query("SELECT * FROM agua_dashboard WHERE id = 1")
    fun getDashboard(): Flow<AguaDashboardEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDashboard(dashboard: AguaDashboardEntity)

    @Query("UPDATE agua_dashboard SET consumidoHoyMl = :ml, porcentajeProgreso = :pct, vasosConsumidos = :vasos, mililitrosRestantes = :restantes WHERE id = 1")
    suspend fun updateConsumo(ml: Int, pct: Int, vasos: Int, restantes: Int)

    @Query("DELETE FROM agua_dashboard")
    suspend fun clearDashboard()
}
