package com.knexus.ergohabit.features.posture.presentation.viewmodel

data class RetrasoSuenoUiState(
    val horasRetraso: Int = 0,
    val horasActuales: Int = 8, // Base recomendada siempre es 8
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
) {
    // Cálculo fijo basado en la meta de 8 horas: 8 - retraso
    val horasResultantes: Int get() = (8 - horasRetraso).coerceAtLeast(1)
    val enLimiteMinimo: Boolean get() = horasResultantes <= 5
}
