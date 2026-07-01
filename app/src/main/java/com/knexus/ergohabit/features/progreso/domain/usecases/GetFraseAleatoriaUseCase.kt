package com.knexus.ergohabit.features.progreso.domain.usecases

import com.knexus.ergohabit.features.progreso.domain.entities.Frase
import com.knexus.ergohabit.features.progreso.domain.repositories.ProgresoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFraseAleatoriaUseCase @Inject constructor(
    private val repository: ProgresoRepository
) {
    operator fun invoke(): Flow<Result<Frase>> = repository.getFraseAleatoria()
}
