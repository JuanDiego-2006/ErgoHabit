package com.knexus.ergohabit.features.progreso.domain.usecases

import com.knexus.ergohabit.features.progreso.domain.repositories.ProgresoRepository
import javax.inject.Inject

class GetHabitosProgresoUseCase @Inject constructor(
    private val repository: ProgresoRepository
) {
    operator fun invoke(idUsuario: Int) = repository.getHabitosProgreso(idUsuario)
}
