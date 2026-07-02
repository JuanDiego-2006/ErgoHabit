package com.knexus.ergohabit.core.database.dao

import androidx.room.*
import com.knexus.ergohabit.core.database.entities.NutricionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NutricionDao {
    @Query("SELECT * FROM nutricion_config WHERE id = 1")
    fun getNutricionConfig(): Flow<NutricionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNutricionConfig(config: NutricionEntity)

    @Query("DELETE FROM nutricion_config")
    suspend fun clearNutricionConfig()

    @Query("UPDATE nutricion_config SET notificacionesHabilitadas = :status WHERE id = 1")
    suspend fun setNotificacionesStatus(status: Boolean)
}
