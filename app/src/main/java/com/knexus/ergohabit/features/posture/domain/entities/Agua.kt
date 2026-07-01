package com.knexus.ergohabit.features.posture.domain.entities

data class DashboardAgua(
    val metaDiariaMl: Int,
    val consumidoHoyMl: Int,
    val porcentajeProgreso: Int,
    val vasosConsumidos: Int,
    val mililitrosRestantes: Int,
    val estaturaActual: Double,
    val pesoActual: Double,
    val historialHoy: List<TomaAgua>,
    val fraseMotivacional: String,
    val tipsHidratacion: List<String>
)

data class TomaAgua(
    val id: Int,
    val cantidadMl: Int,
    val hora: String
)

data class RegistroSemanalAgua(
    val tituloSeccion: String,
    val mensajeMeta: String,
    val datosGrafica: List<ElementoGraficaAgua>
)

data class ElementoGraficaAgua(
    val diaSemana: String,
    val valor: Double,
    val metaCumplida: Boolean,
    val esHoy: Boolean
)
