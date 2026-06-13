package com.knexus.ergohabit.features.posture.presentation.viewmodel

data class RetrasoSuenoUiState(
    val horasRetraso: Int = 3,
    val horasActuales: Int = 8
) {
    val horasResultantes: Int get() = (horasActuales - horasRetraso).coerceAtLeast(1)
    val enLimiteMinimo: Boolean get() = horasResultantes <= 5
}