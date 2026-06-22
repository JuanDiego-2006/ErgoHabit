package com.knexus.ergohabit.features.progreso.domain.entities

data class DetalleHabito(
    val idHabito: Int,
    val titulo: String,
    val metaValor: Float,
    val leyendaPositiva: String,
    val leyendaNegativa: String,
    val registros: List<RegistroHabito>
)

data class RegistroHabito(
    val etiqueta: String,
    val valor: Float,
    val esMetaCumplida: Boolean
)
