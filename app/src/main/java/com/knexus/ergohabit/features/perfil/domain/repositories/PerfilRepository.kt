package com.knexus.ergohabit.features.perfil.domain.repositories

import com.knexus.ergohabit.features.perfil.domain.entities.UsuarioPerfil
import kotlinx.coroutines.flow.Flow

interface PerfilRepository {
    fun getPerfil(idUsuario: Int): Flow<Result<UsuarioPerfil>>
    suspend fun updateFotoPerfil(idUsuario: Int, fotoUri: String): Result<String>
    suspend fun updatePerfil(idUsuario: Int, perfil: UsuarioPerfil): Result<String>
    suspend fun eliminarPerfil(idUsuario: Int): Result<String>
    suspend fun clearLocalProfile()
}
