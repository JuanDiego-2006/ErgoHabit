package com.knexus.ergohabit.features.posture.domain.usecase

import com.knexus.ergohabit.features.posture.domain.entities.Usuario
import com.knexus.ergohabit.features.posture.domain.repository.AuthRepository
import javax.inject.Inject


class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        nombre: String,
        primerApellido: String,
        segundoApellido: String?,
        email: String,
        contrasena: String
    ): Result<Usuario> {
        return repository.register(
            nombre,
            primerApellido,
            segundoApellido,
            email,
            contrasena
        )
    }
}
