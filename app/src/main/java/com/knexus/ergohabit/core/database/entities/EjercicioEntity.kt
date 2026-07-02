package com.knexus.ergohabit.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ejercicio_dashboard")
data class EjercicioEntity(
    @PrimaryKey val id: Int = 1,
    val kmRecorridosText: String,
    val metaKmText: String,
    val porcentajeCumplimiento: Int,
    val caloriasQuemadas: Int,
    val rachaDias: Int,
    val mensajeFaltanteText: String,
    val sugerenciaCaminataText: String,
    val fraseMotivacional: String,
    val lastUpdate: Long = System.currentTimeMillis()
)
