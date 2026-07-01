package com.knexus.ergohabit.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habitos_progreso")
data class HabitoProgresoEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    val icono: String,
    val colorHex: String,
    val porcentaje: Int,
    val tituloSeccion: String? = null,
    val leyendaPositiva: String? = null,
    val leyendaNegativa: String? = null
)
