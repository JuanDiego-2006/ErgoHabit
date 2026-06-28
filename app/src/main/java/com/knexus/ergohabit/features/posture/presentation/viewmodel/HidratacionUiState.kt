package com.knexus.ergohabit.features.posture.presentation.viewmodel

data class HidratacionUiState(
    val mlActuales: Int = 0,
    val mlObjetivo: Int = 2450,
    val vasosObjetivo: Int = 10,
    val fraseMotivacional: String = "",
    val tips: List<String> = emptyList(),
    val isLoading: Boolean = true,
    val isRegistrando: Boolean = false,
    val error: String? = null
) {
    val porcentaje: Float
        get() = if (mlObjetivo > 0) {
            (mlActuales / mlObjetivo.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }

    val porcentajeTexto: Int get() = (porcentaje * 100).toInt()

    val metaCumplida: Boolean get() = mlObjetivo > 0 && mlActuales >= mlObjetivo

    val mlRestantes: Int get() = (mlObjetivo - mlActuales).coerceAtLeast(0)
    val vasosRestantes: Int get() = (mlRestantes / 250.0).toInt()
    val vasosActuales: Int get() = (mlActuales / 250.0).toInt()
}
