package com.knexus.ergohabit.features.tareas.domain.usecases

import com.knexus.ergohabit.features.tareas.domain.entities.TareaEnfoque
import com.knexus.ergohabit.features.tareas.domain.repositories.TareaRepository
import javax.inject.Inject

class CompletarTareaUseCase @Inject constructor(
    private val repository: TareaRepository
) {
    suspend operator fun invoke(idTarea: Int): Result<TareaEnfoque> {
        return repository.completarTarea(idTarea)
    }
}
