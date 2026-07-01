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
}
