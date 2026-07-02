package com.knexus.ergohabit.core.database.dao

import androidx.room.*
import com.knexus.ergohabit.core.database.entities.HabitoProgresoEntity
import com.knexus.ergohabit.core.database.entities.RegistroSemanalEntity
import com.knexus.ergohabit.core.database.entities.FraseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgresoDao {
    @Query("SELECT * FROM habitos_progreso")
    fun getAllHabitos(): Flow<List<HabitoProgresoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitos(habitos: List<HabitoProgresoEntity>)

    @Query("SELECT * FROM habitos_progreso WHERE id = :idHabito")
    suspend fun getHabitoById(idHabito: Int): HabitoProgresoEntity?

    @Query("SELECT * FROM registros_semanales WHERE idHabito = :idHabito")
    fun getRegistrosByHabito(idHabito: Int): Flow<List<RegistroSemanalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegistros(registros: List<RegistroSemanalEntity>)

    @Query("DELETE FROM registros_semanales WHERE idHabito = :idHabito")
    suspend fun deleteRegistrosByHabito(idHabito: Int)

    @Transaction
    suspend fun updateDetalleHabito(idHabito: Int, habit: HabitoProgresoEntity, registros: List<RegistroSemanalEntity>) {
        // Actualizar datos base del hábito
        insertHabitos(listOf(habit))
        // Reemplazar registros semanales
        deleteRegistrosByHabito(idHabito)
        insertRegistros(registros)
    }

    @Query("SELECT * FROM frase_dia LIMIT 1")
    fun getFraseDia(): Flow<FraseEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFrase(frase: FraseEntity)

    @Query("DELETE FROM frase_dia")
    suspend fun clearFrase()
}
