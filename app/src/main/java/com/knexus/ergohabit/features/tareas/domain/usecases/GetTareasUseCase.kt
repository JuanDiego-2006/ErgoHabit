package com.knexus.ergohabit.features.tareas.domain.usecases

import com.knexus.ergohabit.features.tareas.domain.entities.TareaEnfoque
import com.knexus.ergohabit.features.tareas.domain.repositories.TareaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTareasUseCase @Inject constructor(
    private val repository: TareaRepository
) {
    operator fun invoke(idUsuario: Int): Flow<Result<List<TareaEnfoque>>> {
        return repository.getTareas(idUsuario)
    }
}
