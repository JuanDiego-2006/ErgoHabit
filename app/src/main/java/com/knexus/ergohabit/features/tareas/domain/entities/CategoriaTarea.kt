package com.knexus.ergohabit.features.tareas.domain.entities

data class CategoriaTarea(
    val id: Int,
    val nombre: String,
    val icono: String = "📌"
)
