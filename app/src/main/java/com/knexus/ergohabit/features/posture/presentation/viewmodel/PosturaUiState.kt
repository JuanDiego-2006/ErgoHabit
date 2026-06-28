package com.knexus.ergohabit.features.posture.presentation.viewmodel

data class PosturaUiState(
    val anguloPitch: Double = 0.0,
    val anguloRoll: Double = 0.0,
    val esCorrecta: Boolean = true,
    val mensaje: String = "Inactivo",
    val estaMonitoreando: Boolean = false,
    val conteoVibraciones: Int = 0,
    
    // --- NUEVO: Sincronización de Agua ---
    val aguaPorcentaje: Int = 0,
    val aguaMetaTexto: String = "Meta: --",
    val nombreUsuario: String = "Usuario"
)