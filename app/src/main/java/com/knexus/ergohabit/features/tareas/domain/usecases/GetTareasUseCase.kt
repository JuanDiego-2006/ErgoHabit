package com.knexus.ergohabit.features.tareas.domain.usecases

import com.knexus.ergohabit.features.tareas.domain.entities.TareasEstado
import com.knexus.ergohabit.features.tareas.domain.repositories.TareaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTareasUseCase @Inject constructor(
    private val repository: TareaRepository
) {
    operator fun invoke(): Flow<Result<TareasEstado>> = repository.getTareas()
}
