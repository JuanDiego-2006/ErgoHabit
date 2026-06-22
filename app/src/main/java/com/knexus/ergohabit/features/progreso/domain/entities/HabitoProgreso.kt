package com.knexus.ergohabit.features.progreso.domain.entities

data class HabitoProgreso(
    val id: Int,
    val nombre: String,
    val icono: String,
    val colorHex: String,
    val porcentaje: Int
)
