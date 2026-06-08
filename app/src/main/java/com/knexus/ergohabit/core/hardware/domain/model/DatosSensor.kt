package com.knexus.ergohabit.core.hardware.domain.model

/**
 * Modelo que representa los datos crudos de los sensores en español.
 */
data class DatosSensor(
    val inclinacion: Double,
    val balanceo: Double,
    val guinada: Double = 0.0
)
