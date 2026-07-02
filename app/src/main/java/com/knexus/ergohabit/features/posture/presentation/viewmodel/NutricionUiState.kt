package com.knexus.ergohabit.features.posture.presentation.viewmodel

data class NutricionUiState(
    val comidasCompletadas: Int = 0,
    val comidasObjetivo: Int = 3,
    val desayunoCompletado: Boolean = false,
    val comidaCompletada: Boolean = false,
    val cenaCompletada: Boolean = false,
    val horaDesayuno: String = "--:--",
    val horaComida: String = "--:--",
    val horaCena: String = "--:--",
    val mensajeFaltante: String = "",
    val fraseMotivacional: String = "",
    val tips: List<String> = emptyList(),
    val porcentajeBackend: Int = 0,
    val notificacionesHabilitadas: Boolean = true,
    val isLoading: Boolean = true,
    val successMessage: String? = null,
    val error: String? = null
) {
    val porcentaje: Float
        get() = if (porcentajeBackend > 0) {
            (porcentajeBackend / 100f).coerceIn(0f, 1f)
        } else if (comidasObjetivo > 0) {
            (comidasCompletadas / comidasObjetivo.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
    val comidasRestantes: Int get() = (comidasObjetivo - comidasCompletadas).coerceAtLeast(0)
}
