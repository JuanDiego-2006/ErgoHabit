package com.knexus.ergohabit.features.posture.presentation.viewmodel

data class SuenoUiState(
    val horasDormidas: Float = 6.5f,
    val horasRecomendadas: Float = 8f,
    val calidad: String = "Moderada",
    val horaDormir: String = "22:00",
    val horaDespertar: String = "06:00",
    val alarmaActivada: Boolean = true
) {
    val porcentaje: Float get() = (horasDormidas / horasRecomendadas).coerceIn(0f, 1f)
    val horasPlanificadas: Float get() = 8.0f
    val cumpleRecomendacion: Boolean get() = horasPlanificadas >= horasRecomendadas
}