package com.knexus.ergohabit.features.perfil.domain.usecases

import com.knexus.ergohabit.features.perfil.domain.repositories.PerfilRepository
import javax.inject.Inject

class EliminarPerfilUseCase @Inject constructor(
    private val repository: PerfilRepository
) {
    suspend operator fun invoke(idUsuario: Int): Result<String> {
        return repository.eliminarPerfil(idUsuario)
    }
}
