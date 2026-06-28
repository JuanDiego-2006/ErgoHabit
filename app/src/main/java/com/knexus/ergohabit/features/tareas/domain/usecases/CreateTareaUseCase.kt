package com.knexus.ergohabit.features.tareas.domain.usecases

import com.knexus.ergohabit.features.tareas.domain.repositories.TareaRepository
import javax.inject.Inject

class CreateTareaUseCase @Inject constructor(
    private val repository: TareaRepository
) {
    suspend operator fun invoke(titulo: String, categoria: String, duracionMinutos: Int): Result<String> {
        return repository.createTarea(titulo, categoria, duracionMinutos)
    }
}
