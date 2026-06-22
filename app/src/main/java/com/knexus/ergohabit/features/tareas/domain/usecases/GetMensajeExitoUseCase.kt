package com.knexus.ergohabit.features.tareas.domain.usecases

import com.knexus.ergohabit.features.tareas.domain.repositories.TareaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMensajeExitoUseCase @Inject constructor(
    private val repository: TareaRepository
) {
    operator fun invoke(): Flow<Result<String>> {
        return repository.getMensajeExito()
    }
}
