package com.knexus.ergohabit.features.posture.presentation.viewmodel

data class PosturaUiState(
    val anguloPitch: Double = 0.0,
    val anguloRoll: Double = 0.0,
    val esCorrecta: Boolean = true,
    val mensaje: String = "Inactivo",
    val estaMonitoreando: Boolean = false,
    val conteoVibraciones: Int = 0,
    val gradosDisplay: Int = 0          // ← grados reales a mostrar en UI
)