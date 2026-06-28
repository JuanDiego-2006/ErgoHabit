package com.knexus.ergohabit.features.perfil.data.repositories

import com.knexus.ergohabit.features.perfil.data.datasource.api.PerfilApi
import com.knexus.ergohabit.features.perfil.domain.entities.UsuarioPerfil
import com.knexus.ergohabit.features.perfil.domain.repositories.PerfilRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PerfilRepositoryImpl @Inject constructor(
    private val api: PerfilApi
) : PerfilRepository {

    override fun getPerfil(idUsuario: Int): Flow<Result<UsuarioPerfil>> = flow {
        try {
            val perfil = api.getPerfil(idUsuario)
            emit(Result.success(perfil))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun updateFotoPerfil(idUsuario: Int, fotoUri: String): Result<String> {
        return Result.failure(UnsupportedOperationException("Subida de foto pendiente de implementar"))
    }

    override suspend fun updatePerfil(perfil: UsuarioPerfil): Result<Unit> {
        return Result.failure(UnsupportedOperationException("Actualización de perfil pendiente de implementar"))
    }
}
