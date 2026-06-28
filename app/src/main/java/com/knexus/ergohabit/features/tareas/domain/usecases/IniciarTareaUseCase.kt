package com.knexus.ergohabit.features.tareas.domain.usecases

import com.knexus.ergohabit.features.tareas.domain.repositories.TareaRepository
import javax.inject.Inject

class IniciarTareaUseCase @Inject constructor(
    private val repository: TareaRepository
) {
    suspend operator fun invoke(idTarea: Int): Result<String> {
        return repository.iniciarTarea(idTarea)
    }
}
