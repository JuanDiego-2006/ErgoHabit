package com.knexus.ergohabit.features.tareas.domain.repositories

import com.knexus.ergohabit.features.tareas.domain.entities.TareaEnfoque
import kotlinx.coroutines.flow.Flow

interface TareaRepository {
    fun getTareas(idUsuario: Int): Flow<Result<List<TareaEnfoque>>>
    fun createTarea(tarea: TareaEnfoque): Flow<Result<TareaEnfoque>>
}
