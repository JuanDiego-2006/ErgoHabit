package com.knexus.ergohabit.features.tareas.domain.usecases

import com.knexus.ergohabit.features.tareas.domain.repositories.TareaRepository
import javax.inject.Inject

class GetMensajeExitoUseCase @Inject constructor(
    private val repository: TareaRepository
) {
    suspend operator fun invoke(): Result<String> = repository.getMensajeExito()
}
