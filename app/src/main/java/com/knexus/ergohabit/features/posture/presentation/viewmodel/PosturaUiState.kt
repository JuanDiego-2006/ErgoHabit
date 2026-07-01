package com.knexus.ergohabit.features.posture.presentation.viewmodel

data class ResumenHabitoUi(
    val emoji: String,
    val nombre: String,
    val meta: String,
    val pct: Int,
    val completado: Boolean
)

data class PosturaUiState(
    val anguloPitch: Double = 0.0,
    val anguloRoll: Double = 0.0,
    val esCorrecta: Boolean = true,
    val mensaje: String = "Inactivo",
    val estaMonitoreando: Boolean = false,
    val conteoVibraciones: Int = 0,
    val gradosDisplay: Int = 0,
    val mensajeCamara: String = "",
    val camaraActiva: Boolean = false,
    val alertaPorCamara: Boolean = false,
    val nombreUsuario: String = "",
    val habitosCompletados: Int = 0,
    val habitosTotal: Int = 4,
    val rachaDias: Int = 0,
    val resumenHabitos: List<ResumenHabitoUi> = emptyList(),
    val cargandoDashboard: Boolean = true
    val conteoVibraciones: Int = 0,

    // --- NUEVO: Sincronización de Agua ---
    val aguaPorcentaje: Int = 0,
    val aguaMetaTexto: String = "Meta: --",
    val nombreUsuario: String = "Usuario"
)
