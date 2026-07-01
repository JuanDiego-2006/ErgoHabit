package com.knexus.ergohabit.features.tareas.domain.usecases

import com.knexus.ergohabit.features.tareas.domain.repositories.TareaRepository
import com.knexus.ergohabit.core.database.entities.TareaProgresoEntity
import javax.inject.Inject

class SaveLocalProgressUseCase @Inject constructor(private val repository: TareaRepository) {
    suspend operator fun invoke(idTarea: Int, restante: Int, inicial: Int, endTime: Long) {
        repository.saveLocalProgress(idTarea, restante, inicial, endTime)
    }
}

class GetLocalProgressUseCase @Inject constructor(private val repository: TareaRepository) {
    suspend operator fun invoke(idTarea: Int): TareaProgresoEntity? {
        return repository.getLocalProgress(idTarea)
    }
}

class ClearLocalProgressUseCase @Inject constructor(private val repository: TareaRepository) {
    suspend operator fun invoke(idTarea: Int) {
        repository.clearLocalProgress(idTarea)
    }
}
