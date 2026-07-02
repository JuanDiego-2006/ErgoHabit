package com.knexus.ergohabit.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ejercicio_sesion_actual")
data class EjercicioSesionEntity(
    @PrimaryKey val id: Int = 1,
    val pasosAcumulados: Int,
    val kmAcumulados: Double,
    val lastUpdate: Long = System.currentTimeMillis()
)
