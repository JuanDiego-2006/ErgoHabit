package com.knexus.ergohabit.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "agua_dashboard")
data class AguaDashboardEntity(
    @PrimaryKey val id: Int = 1,
    val metaDiariaMl: Int,
    val consumidoHoyMl: Int,
    val porcentajeProgreso: Int,
    val vasosConsumidos: Int,
    val mililitrosRestantes: Int,
    val fraseMotivacional: String,
    val pesoActual: Double,
    val estaturaActual: Double,
    val notificacionesActivas: Boolean = true
)
