package com.knexus.ergohabit.features.posture.presentation.viewmodel

data class NutricionUiState(
    val comidasCompletadas: Int = 2,
    val comidasObjetivo: Int = 3,
    val desayunoCompletado: Boolean = true,
    val comidaCompletada: Boolean = true,
    val cenaCompletada: Boolean = false
) {
    val porcentaje: Float get() = comidasCompletadas / comidasObjetivo.toFloat()
    val comidasRestantes: Int get() = comidasObjetivo - comidasCompletadas
}