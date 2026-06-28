package com.knexus.ergohabit.features.progreso.data.models

import com.google.gson.annotations.SerializedName

data class ProgresoDiarioDto(
    @SerializedName("nombreUsuario") val nombreUsuario: String,
    @SerializedName("totalAlertasPostura") val totalAlertasPostura: Int,
    @SerializedName("estadoPostura") val estadoPostura: String,
    @SerializedName("rachaDias") val rachaDias: Int,
    @SerializedName("metaAguaMl") val metaAguaMl: Int,
    @SerializedName("aguaConsumidaMl") val aguaConsumidaMl: Int,
    @SerializedName("sugerenciaAguaPesoMl") val sugerenciaAguaPesoMl: Int,
    @SerializedName("porcentajeAgua") val porcentajeAgua: Double,
    @SerializedName("horasSuenoRegistradas") val horasSuenoRegistradas: Double,
    @SerializedName("porcentajeSueno") val porcentajeSueno: Double,
    @SerializedName("metaEjercicioKm") val metaEjercicioKm: Double,
    @SerializedName("kmEjercicioRecorridos") val kmEjercicioRecorridos: Double,
    @SerializedName("caloriasEjercicioQuemadas") val caloriasEjercicioQuemadas: Int,
    @SerializedName("porcentajeEjercicio") val porcentajeEjercicio: Double,
    @SerializedName("comidasCompletadasHoy") val comidasCompletadasHoy: Int,
    @SerializedName("metaComidasTotales") val metaComidasTotales: Int,
    @SerializedName("porcentajeNutricion") val porcentajeNutricion: Double
)
