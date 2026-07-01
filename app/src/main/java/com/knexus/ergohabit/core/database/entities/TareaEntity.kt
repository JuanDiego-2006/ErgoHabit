package com.knexus.ergohabit.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tareas")
data class TareaEntity(
    @PrimaryKey val id: Int,
    val titulo: String,
    val categoria: String,
    val duracionText: String,
    val idEstado: Int,
    val duracionMinutos: Int,
    val fechaInicioCronometro: String? = null,
    val esCompletada: Boolean
)
