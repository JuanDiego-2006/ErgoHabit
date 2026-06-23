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
            // Intentar obtener de la API
            val perfil = api.getPerfil(idUsuario)
            emit(Result.success(perfil))
        } catch (e: Exception) {
            // Datos de prueba (Mock) si falla la API
            val mockPerfil = UsuarioPerfil(
                id = idUsuario,
                nombre = "ailyn",
                primerApellido = "GARCÍA",
                segundoApellido = "MÉNDEZ",
                correo = "ailyn@gmail.com",
                fotoUrl = null
            )
            emit(Result.success(mockPerfil))
        }
    }

    override suspend fun updateFotoPerfil(idUsuario: Int, fotoUri: String): Result<String> {
        return try {
            // Aquí iría la lógica para subir la imagen al servidor
            Result.success(fotoUri)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePerfil(perfil: UsuarioPerfil): Result<Unit> {
        return try {
            // Aquí iría la llamada a la API para actualizar los datos
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
