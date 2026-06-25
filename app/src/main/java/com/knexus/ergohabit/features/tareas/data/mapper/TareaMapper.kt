package com.knexus.ergohabit.features.tareas.data.mapper

import com.knexus.ergohabit.features.tareas.data.models.TareaDto
import com.knexus.ergohabit.features.tareas.data.models.TareasResponseDto
import com.knexus.ergohabit.features.tareas.domain.entities.TareaEnfoque
import com.knexus.ergohabit.features.tareas.domain.entities.TareasEstado

fun TareaDto.toDomain(): TareaEnfoque {
    val dText = this.duracionText ?: "0 min"
    val minutos = dText.replace(" min", "").toIntOrNull() ?: 0
    return TareaEnfoque(
        id = this.idTarea ?: 0,
        titulo = this.titulo ?: "Sin título",
        categoria = this.categoria ?: "General",
        duracionText = dText,
        idEstado = this.idEstado ?: 1,
        duracionMinutos = minutos,
        fechaInicioCronometro = this.fechaInicioCronometro
    )
}

fun TareasResponseDto.toDomain(): TareasEstado {
    return TareasEstado(
        pendientes = this.pendientes?.map { it.toDomain() } ?: emptyList(),
        completadas = this.completadas?.map { it.toDomain() } ?: emptyList(),
        totalPendientesText = this.totalPendientesText ?: "PENDIENTES · 0",
        totalCompletadasText = this.totalCompletadasText ?: "COMPLETADAS · 0"
    )
}
