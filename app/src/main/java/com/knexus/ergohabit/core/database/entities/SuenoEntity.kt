package com.knexus.ergohabit.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sueno_dashboard")
data class SuenoEntity(
    @PrimaryKey val id: Int = 1, // Solo guardamos un dashboard (el del usuario actual)
    val horasPlanificadas: Int,
    val horasDormidasReales: Double,
    val despertoATiempo: Boolean,
    val horaDormirConfigurada: String,
    val horaDespertarConfigurada: String,
    val porcentajeCumplimiento: Int,
    val fraseMotivacional: String,
    val lastUpdated: Long = System.currentTimeMillis()
)
