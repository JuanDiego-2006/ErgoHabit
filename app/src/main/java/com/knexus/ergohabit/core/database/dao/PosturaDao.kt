package com.knexus.ergohabit.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.knexus.ergohabit.core.database.entities.PosturaEntity

@Dao
interface PosturaDao {
    @Insert
    suspend fun insertarAlerta(alerta: PosturaEntity)

    @Query("SELECT COUNT(*) FROM postura_alertas WHERE sincronizado = 0")
    suspend fun obtenerConteoNoSincronizados(): Int

    @Query("UPDATE postura_alertas SET sincronizado = 1 WHERE sincronizado = 0")
    suspend fun marcarComoSincronizados()

    @Query("DELETE FROM postura_alertas WHERE sincronizado = 1")
    suspend fun eliminarSincronizados()
}
