package com.knexus.ergohabit.features.tareas.domain.entities

data class TareaEnfoque(
    val id: Int,
    val titulo: String,
    val categoria: String,
    val duracionText: String,
    val idEstado: Int,
    val duracionMinutos: Int = 0,
    val fechaInicioCronometro: String? = null
)

data class TareasEstado(
    val pendientes: List<TareaEnfoque>,
    val completadas: List<TareaEnfoque>,
    val totalPendientesText: String,
    val totalCompletadasText: String
)
