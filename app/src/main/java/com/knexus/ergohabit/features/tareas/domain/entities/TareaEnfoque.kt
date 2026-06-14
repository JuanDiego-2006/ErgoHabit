package com.knexus.ergohabit.features.tareas.domain.entities

data class TareaEnfoque(
    val id: Int,
    val idUsuario: Int,
    val idEstado: Int,
    val titulo: String,
    val duracionTarea: Int,
    val idCategoria: Int,
    val fechaCreacion: String
)
