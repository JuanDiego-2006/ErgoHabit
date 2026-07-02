package com.knexus.ergohabit.features.perfil.domain.usecases

import com.knexus.ergohabit.features.perfil.domain.repositories.PerfilRepository
import javax.inject.Inject

class UpdateFotoPerfilUseCase @Inject constructor(
    private val repository: PerfilRepository
) {
    suspend operator fun invoke(idUsuario: Int, fotoUri: String): Result<String> {
        return repository.updateFotoPerfil(idUsuario, fotoUri)
    }
}
