package com.knexus.ergohabit.core.hardware.domain.model

data class ResultadoPosturaCamara(
    val personaDetectada: Boolean,
    val esCorrecta: Boolean,
    val motivo: String
)
