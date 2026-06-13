package com.knexus.ergohabit.features.posture.presentation.viewmodel

data class HidratacionUiState(
    val mlActuales: Int = 0,
    val mlObjetivo: Int = 2450,
    val vasosObjetivo: Int = 10
) {
    val porcentaje: Float get() = if (mlObjetivo > 0) mlActuales / mlObjetivo.toFloat() else 0f
    val mlRestantes: Int get() = (mlObjetivo - mlActuales).coerceAtLeast(0)
    val vasosRestantes: Int get() = (mlRestantes / 245.0).toInt()
    val vasosActuales: Int get() = (mlActuales / 245.0).toInt()
}