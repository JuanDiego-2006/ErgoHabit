package com.knexus.ergohabit.features.tareas.domain.usecases

import com.knexus.ergohabit.features.tareas.domain.entities.TareaEnfoque
import com.knexus.ergohabit.features.tareas.domain.repositories.TareaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CompletarTareaUseCase @Inject constructor(
    private val repository: TareaRepository
) {
    operator fun invoke(idTarea: Int): Flow<Result<TareaEnfoque>> {
        return repository.completarTarea(idTarea)
    }
}
