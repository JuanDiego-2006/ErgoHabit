package com.knexus.ergohabit.core.database.dao

import androidx.room.*
import com.knexus.ergohabit.core.database.entities.SuenoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SuenoDao {
    @Query("SELECT * FROM sueno_dashboard WHERE id = 1")
    fun getSuenoDashboard(): Flow<SuenoEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSuenoDashboard(sueno: SuenoEntity)

    @Query("DELETE FROM sueno_dashboard")
    suspend fun clearSuenoDashboard()

    @Query("UPDATE sueno_dashboard SET isAlarmActive = :active, isSoundEnabled = :active WHERE id = 1")
    suspend fun updateAlarmStatus(active: Boolean)

    @Query("UPDATE sueno_dashboard SET isSoundEnabled = :enabled WHERE id = 1")
    suspend fun setSoundStatus(enabled: Boolean)

    @Query("UPDATE sueno_dashboard SET notificacionesHabilitadas = :habilitadas WHERE id = 1")
    suspend fun setNotificacionesStatus(habilitadas: Boolean)
}
