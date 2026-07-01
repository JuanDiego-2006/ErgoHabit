package com.knexus.ergohabit.features.perfil.domain.usecases

import com.knexus.ergohabit.features.perfil.domain.entities.UsuarioPerfil
import com.knexus.ergohabit.features.perfil.domain.repositories.PerfilRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPerfilUseCase @Inject constructor(
    private val repository: PerfilRepository
) {
    operator fun invoke(idUsuario: Int): Flow<Result<UsuarioPerfil>> {
        return repository.getPerfil(idUsuario)
    }
}
