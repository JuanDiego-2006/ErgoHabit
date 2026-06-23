package com.knexus.ergohabit.features.tareas.data.mapper

import com.knexus.ergohabit.features.tareas.data.models.TareaDto
import com.knexus.ergohabit.features.tareas.data.models.TareasResponseDto
import com.knexus.ergohabit.features.tareas.domain.entities.TareaEnfoque
import com.knexus.ergohabit.features.tareas.domain.entities.TareasEstado

fun TareaDto.toDomain(): TareaEnfoque {
    // Extraer minutos del texto "30 min"
    val minutos = this.duracionText.replace(" min", "").toIntOrNull() ?: 0
    return TareaEnfoque(
        id = this.idTarea,
        titulo = this.titulo,
        categoria = this.categoria,
        duracionText = this.duracionText,
        idEstado = this.idEstado,
        duracionMinutos = minutos
    )
}

fun TareasResponseDto.toDomain(): TareasEstado {
    return TareasEstado(
        pendientes = this.pendientes.map { it.toDomain() },
        completadas = this.completadas.map { it.toDomain() },
        totalPendientesText = this.totalPendientesText,
        totalCompletadasText = this.totalCompletadasText
    )
}
