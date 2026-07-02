package com.knexus.ergohabit.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nutricion_config")
data class NutricionEntity(
    @PrimaryKey val id: Int = 1,
    val userId: Int, // Relación con el usuario (lógica)
    val horaDesayuno: String,
    val horaComida: String,
    val horaCena: String,
    val chequeoDesayuno: Boolean = false,
    val chequeoComida: Boolean = false,
    val chequeoCena: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)
