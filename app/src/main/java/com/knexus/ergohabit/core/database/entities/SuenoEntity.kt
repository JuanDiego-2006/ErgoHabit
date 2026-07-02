package com.knexus.ergohabit.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sueno_dashboard")
data class SuenoEntity(
    @PrimaryKey val id: Int = 1,
    val userId: Int,
    val horasPlanificadas: Int,
    val horasDormidasReales: Double,
    val despertoATiempo: Boolean,
    val horaDormirConfigurada: String,
    val horaDespertarConfigurada: String,
    val porcentajeCumplimiento: Int,
    val fraseMotivacional: String,
    val isAlarmActive: Boolean = false,
    val isSoundEnabled: Boolean = false,
    val notificacionesHabilitadas: Boolean = true, // NUEVO: Control maestro
    val lastUpdated: Long = System.currentTimeMillis()
)
