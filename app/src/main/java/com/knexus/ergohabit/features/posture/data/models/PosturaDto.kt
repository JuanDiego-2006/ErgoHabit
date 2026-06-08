package com.knexus.ergohabit.features.posture.data.models

import com.google.gson.annotations.SerializedName

/**
 * Objeto de transferencia de datos para el servidor.
 * Incluye el conteo de vibraciones para generar estadísticas en el back-end.
 */
data class PosturaDto(
    @SerializedName("inclinacion")
    val inclinacion: Double,
    @SerializedName("balanceo")
    val balanceo: Double,
    @SerializedName("esCorrecta")
    val esCorrecta: Boolean,
    @SerializedName("fechaRegistro")
    val fechaRegistro: Long,
    @SerializedName("conteoVibraciones")
    val conteoVibraciones: Int
)
