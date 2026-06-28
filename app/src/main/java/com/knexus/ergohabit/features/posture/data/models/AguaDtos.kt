package com.knexus.ergohabit.features.posture.data.models

import com.google.gson.annotations.SerializedName

data class RegistrarTomaRequest(
    @SerializedName("cantidadMl") val cantidadMl: Int
)

data class ConfigurarMetaRequest(
    @SerializedName("tipo") val tipo: String,
    @SerializedName("metaMl") val metaMl: Int? = null,
    @SerializedName("peso") val peso: Double? = null,
    @SerializedName("estatura") val estatura: Double? = null
)

data class DashboardAguaResponse(
    @SerializedName("metaDiariaMl") val metaDiariaMl: Int,
    @SerializedName("consumidoHoyMl") val consumidoHoyMl: Int,
    @SerializedName("porcentajeProgreso") val porcentajeProgreso: Int,
    @SerializedName("vasosConsumidos") val vasosConsumidos: Int,
    @SerializedName("mililitrosRestantes") val mililitrosRestantes: Int,
    @SerializedName("estaturaActual") val estaturaActual: Double,
    @SerializedName("pesoActual") val pesoActual: Double,
    @SerializedName("historialHoy") val historialHoy: List<TomaCronologica>,
    @SerializedName("fraseMotivacional") val fraseMotivacional: String,
    @SerializedName("tipsHidratacion") val tipsHidratacion: List<String>
)

data class TomaCronologica(
    @SerializedName("id") val id: Int,
    @SerializedName("cantidadMl") val cantidadMl: Int,
    @SerializedName("hora") val hora: String
)

data class AguaMensajeResponse(
    @SerializedName("mensaje") val mensaje: String
)

data class HistorialHabitoResponse(
    @SerializedName("tituloSeccion") val tituloSeccion: String,
    @SerializedName("mensajeMeta") val mensajeMeta: String,
    @SerializedName("datosGrafica") val datosGrafica: List<ElementoBarraGrafica>
)

data class ElementoBarraGrafica(
    @SerializedName("diaSemana") val diaSemana: String,
    @SerializedName("valor") val valor: Double,
    @SerializedName("metaCumplida") val metaCumplida: Boolean,
    @SerializedName("esHoy") val esHoy: Boolean
)
