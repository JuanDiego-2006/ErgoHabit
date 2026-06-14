package com.knexus.ergohabit.features.tareas.data.mapper

import com.knexus.ergohabit.features.tareas.data.models.TareaDto
import com.knexus.ergohabit.features.tareas.domain.entities.TareaEnfoque

fun TareaDto.toDomain(): TareaEnfoque {
    return TareaEnfoque(
        id = this.idTarea,
        idUsuario = this.idUsuario,
        idEstado = this.idEstado,
        titulo = this.titulo,
        duracionTarea = this.duracionTarea,
        idCategoria = this.idCategoria,
        fechaCreacion = this.fechaCreacion
    )
}

fun TareaEnfoque.toDto(): TareaDto {
    return TareaDto(
        idTarea = this.id,
        idUsuario = this.idUsuario,
        idEstado = this.idEstado,
        titulo = this.titulo,
        duracionTarea = this.duracionTarea,
        idCategoria = this.idCategoria,
        fechaCreacion = this.fechaCreacion
    )
}
