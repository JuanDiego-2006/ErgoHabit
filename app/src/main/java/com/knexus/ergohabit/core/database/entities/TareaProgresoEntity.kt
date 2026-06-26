package com.knexus.ergohabit.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tareas_progreso")
data class TareaProgresoEntity(
    @PrimaryKey val idTarea: Int,
    val segundosRestantes: Int,
    val segundosIniciales: Int,
    val targetEndTimeMs: Long = -1L // Timestamp en ms para cuando la tarea debe terminar
)
