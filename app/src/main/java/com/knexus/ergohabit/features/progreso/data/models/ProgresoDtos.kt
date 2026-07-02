package com.knexus.ergohabit.features.progreso.data.models

import com.google.gson.annotations.SerializedName

data class HabitoProgresoDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("icono") val icono: String,
    @SerializedName("color_hex") val colorHex: String,
    @SerializedName("porcentaje") val porcentaje: Int
)

data class ProgresoDiaDto(
    @SerializedName("dia") val dia: Int,
    @SerializedName("valor") val valor: Float
)

data class TendenciaGeneralDto(
    @SerializedName("porcentaje") val porcentaje: String,
    @SerializedName("datos") val datos: List<ProgresoDiaDto>
)

data class DetalleHabitoDto(
    @SerializedName("id_habito") val idHabito: Int,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("meta_valor") val metaValor: Float,
    @SerializedName("leyenda_positiva") val leyendaPositiva: String,
    @SerializedName("leyenda_negativa") val leyendaNegativa: String,
    @SerializedName("registros") val registros: List<RegistroHabitoDto>
)

data class RegistroHabitoDto(
    @SerializedName("etiqueta") val etiqueta: String,
    @SerializedName("valor") val valor: Float,
    @SerializedName("es_meta_cumplida") val esMetaCumplida: Boolean
)


data class HabitoProgresoSemanalDto(
    @SerializedName("tituloSeccion") val tituloSeccion: String? = null,
    @SerializedName("mensajeMeta") val mensajeMeta: String,
    @SerializedName("datosGrafica") val datosGrafica: List<DatoGraficaDto>
)

data class DatoGraficaDto(
    @SerializedName("diaSemana") val diaSemana: String,
    @SerializedName("valor") val valor: Float? = null,
    @SerializedName("totalAlertas") val totalAlertas: Int? = null,
    @SerializedName("metaCumplida") val metaCumplida: Boolean? = null,
    @SerializedName("esHoy") val esHoy: Boolean
)
