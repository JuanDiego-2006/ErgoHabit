package com.knexus.ergohabit.features.posture.presentation.viewmodel

data class ActividadUiState(
    val kmActuales: Float = 5.80f,
    val kmObjetivo: Float = 8.0f,
    val calorias: Int = 290,
    val rachasDias: Int = 5
) {
    val porcentaje: Float get() = (kmActuales / kmObjetivo).coerceIn(0f, 1f)
    val kmRestantes: Float get() = (kmObjetivo - kmActuales).coerceAtLeast(0f)
}