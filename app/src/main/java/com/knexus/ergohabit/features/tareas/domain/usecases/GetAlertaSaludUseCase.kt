package com.knexus.ergohabit.features.tareas.domain.usecases

import com.knexus.ergohabit.features.tareas.domain.entities.AlertaSalud
import com.knexus.ergohabit.features.tareas.domain.repositories.TareaRepository
import javax.inject.Inject

class GetAlertaSaludUseCase @Inject constructor(
    private val repository: TareaRepository
) {
    suspend operator fun invoke(idTarea: Int): Result<AlertaSalud> {
        return repository.getInfoCronometro(idTarea)
    }
}
