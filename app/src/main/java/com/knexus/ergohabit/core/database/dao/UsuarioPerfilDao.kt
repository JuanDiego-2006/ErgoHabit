package com.knexus.ergohabit.core.database.dao

import androidx.room.*
import com.knexus.ergohabit.core.database.entities.UsuarioPerfilEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioPerfilDao {
    @Query("SELECT * FROM usuario_perfil WHERE id = :idUsuario")
    fun getPerfil(idUsuario: Int): Flow<UsuarioPerfilEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerfil(perfil: UsuarioPerfilEntity)

    @Query("DELETE FROM usuario_perfil")
    suspend fun clearPerfil()
}
