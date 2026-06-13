package com.knexus.ergohabit.features.posture.presentation.viewmodel

data class ConfigHorarioUiState(
    val horas: Int = 6,
    val minutos: Int = 0
) {
    val horaDespertar: String get() = "${horas.toString().padStart(2, '0')}:${minutos.toString().padStart(2, '0')}"
    val horaDormir: String get() {
        val h = (horas - 8 + 24) % 24
        return "${h.toString().padStart(2, '0')}:${minutos.toString().padStart(2, '0')}"
    }
}