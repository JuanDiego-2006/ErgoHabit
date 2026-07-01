package com.knexus.ergohabit.features.perfil.domain.usecases

import com.knexus.ergohabit.features.perfil.domain.entities.UsuarioPerfil
import com.knexus.ergohabit.features.perfil.domain.repositories.PerfilRepository
import javax.inject.Inject

class UpdatePerfilUseCase @Inject constructor(
    private val repository: PerfilRepository
) {
    suspend operator fun invoke(idUsuario: Int, perfil: UsuarioPerfil): Result<String> {
        return repository.updatePerfil(idUsuario, perfil)
    }
}
