package com.knexus.ergohabit.features.posture.domain.entities

/**
 * Entidad de dominio que representa el estado de la postura del usuario.
 */
data class EntidadPostura(
    val anguloPitch: Double,
    val anguloRoll: Double,
    val esCorrecta: Boolean,
    val mensaje: String,
    val conteoVibraciones: Int = 0
)
