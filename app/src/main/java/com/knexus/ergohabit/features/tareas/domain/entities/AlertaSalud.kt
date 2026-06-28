package com.knexus.ergohabit.features.tareas.domain.entities


data class AlertaSalud(
    val frase: String,
    val accion: String,
    val requierePostura: Boolean
)
