package com.knexus.ergohabit.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "frase_dia")
data class FraseEntity(
    @PrimaryKey val id: Int,
    val categoria: String,
    val texto: String,
    val fechaGuardado: Long = System.currentTimeMillis()
)
