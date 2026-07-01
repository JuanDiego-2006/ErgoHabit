package com.knexus.ergohabit.features.posture.presentation.viewmodel

data class SuenoUiState(
    val horasDormidas: Float = 0f,
    val horasRecomendadas: Float = 8f,
    val calidad: String = "—",
    val horaDormir: String = "--:--",
    val horaDespertar: String = "--:--",
    val alarmaActivada: Boolean = false,
    val fraseMotivacional: String = "",
    val tips: List<String> = emptyList(),
    val isLoading: Boolean = true,
    val mostrarAlarma: Boolean = false,
    val successMessage: String? = null,
    val error: String? = null
) {
    val porcentaje: Float get() = if (horasRecomendadas > 0) {
        (horasDormidas / horasRecomendadas).coerceIn(0f, 1f)
    } else {
        0f
    }
    val horasPlanificadas: Float get() = horasRecomendadas
    val cumpleRecomendacion: Boolean get() = horasDormidas >= horasRecomendadas
}
