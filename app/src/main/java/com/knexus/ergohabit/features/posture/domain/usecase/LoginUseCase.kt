package com.knexus.ergohabit.features.posture.domain.usecase

import com.knexus.ergohabit.features.posture.domain.entities.Sesion
import com.knexus.ergohabit.features.posture.domain.repository.AuthRepository
import javax.inject.Inject


class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<Sesion> {
        return repository.login(email, password)
    }
}
