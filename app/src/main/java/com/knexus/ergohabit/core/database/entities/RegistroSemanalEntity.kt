package com.knexus.ergohabit.core.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "registros_semanales",
    foreignKeys = [
        ForeignKey(
            entity = HabitoProgresoEntity::class,
            parentColumns = ["id"],
            childColumns = ["idHabito"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["idHabito"])]
)
data class RegistroSemanalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val idHabito: Int,
    val etiqueta: String,
    val valor: Float,
    val esMetaCumplida: Boolean
)
