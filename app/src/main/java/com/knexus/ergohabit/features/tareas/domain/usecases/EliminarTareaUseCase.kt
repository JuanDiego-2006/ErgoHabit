package com.knexus.ergohabit.features.tareas.domain.usecases

import com.knexus.ergohabit.features.tareas.domain.repositories.TareaRepository
import javax.inject.Inject

class EliminarTareaUseCase @Inject constructor(
    private val repository: TareaRepository
) {
    suspend operator fun invoke(idTarea: Int): Result<String> {
        return repository.eliminarTarea(idTarea)
    }
}
