package com.knexus.ergohabit.features.posture.data.models

import com.google.gson.annotations.SerializedName

data class HabitosDashboardAguaResponse(
    @SerializedName("metaDiariaMl") val metaDiariaMl: Int,
    @SerializedName("consumidoHoyMl") val consumidoHoyMl: Int,
    @SerializedName("porcentajeProgreso") val porcentajeProgreso: Int,
    @SerializedName("vasosConsumidos") val vasosConsumidos: Int,
    @SerializedName("mililitrosRestantes") val mililitrosRestantes: Int,
    @SerializedName("estaturaActual") val estaturaActual: Double = 0.0,
    @SerializedName("pesoActual") val pesoActual: Double = 0.0,
    @SerializedName("historialHoy") val historialHoy: List<TomaCronologicaDto> = emptyList(),
    @SerializedName("fraseMotivacional") val fraseMotivacional: String = "",
    @SerializedName("tipsHidratacion") val tipsHidratacion: List<String> = emptyList()
)

data class TomaCronologicaDto(
    @SerializedName("id") val id: Int,
    @SerializedName("cantidadMl") val cantidadMl: Int,
    @SerializedName("hora") val hora: String
)

data class RegistrarTomaRequest(
    @SerializedName("cantidadMl") val cantidadMl: Int
)

data class ConfigurarMetaAguaRequest(
    @SerializedName("tipo") val tipo: String,
    @SerializedName("metaMl") val metaMl: Int? = null,
    @SerializedName("peso") val peso: Double? = null,
    @SerializedName("estatura") val estatura: Double? = null
)

data class MensajeResponse(
    @SerializedName("mensaje") val mensaje: String
)

data class SuenoResponse(
    @SerializedName("horasPlanificadas") val horasPlanificadas: Int,
    @SerializedName("horasDormidasReales") val horasDormidasReales: Double,
    @SerializedName("despertoATiempo") val despertoATiempo: Boolean,
    @SerializedName("horaDormirConfigurada") val horaDormirConfigurada: String,
    @SerializedName("horaDespertarConfigurada") val horaDespertarConfigurada: String,
    @SerializedName("porcentajeCumplimiento") val porcentajeCumplimiento: Int,
    @SerializedName("fraseMotivacional") val fraseMotivacional: String = "",
    @SerializedName("tipsSueno") val tipsSueno: List<String> = emptyList(),
    val isAlarmActiveLocal: Boolean = false,
    val isSoundEnabledLocal: Boolean = false,
    val notificacionesHabilitadasLocal: Boolean = true
)

data class SuenoRequest(
    @SerializedName("horaDespertar") val horaDespertar: String,
    @SerializedName("horaDormir") val horaDormir: String
)

data class HistorialHabitoResponse(
    @SerializedName("tituloSeccion") val tituloSeccion: String,
    @SerializedName("mensajeMeta") val mensajeMeta: String,
    @SerializedName("datosGrafica") val datosGrafica: List<ElementoBarraGraficaDto>
)

data class ElementoBarraGraficaDto(
    @SerializedName("diaSemana") val diaSemana: String,
    @SerializedName("valor") val valor: Double,
    @SerializedName("metaCumplida") val metaCumplida: Boolean,
    @SerializedName("esHoy") val esHoy: Boolean
)

data class NutricionDashboardResponse(
    @SerializedName("comidasCompletadasText") val comidasCompletadasText: String,
    @SerializedName("porcentajeCumplimiento") val porcentajeCumplimiento: Int,
    @SerializedName("mensajeFaltanteText") val mensajeFaltanteText: String,
    @SerializedName("horaDesayunoConfigurada") val horaDesayunoConfigurada: String,
    @SerializedName("horaComidaConfigurada") val horaComidaConfigurada: String,
    @SerializedName("horaCenaConfigurada") val horaCenaConfigurada: String,
    @SerializedName("chequeoDesayuno") val chequeoDesayuno: Boolean,
    @SerializedName("chequeoComida") val chequeoComida: Boolean,
    @SerializedName("chequeoCena") val chequeoCena: Boolean,
    @SerializedName("fraseMotivacional") val fraseMotivacional: String = "",
    @SerializedName("tipsNutricion") val tipsNutricion: List<String> = emptyList(),
    val notificacionesHabilitadasLocal: Boolean = true
)

data class ConfigurarNutricionRequest(
    @SerializedName("horaDesayuno") val horaDesayuno: String,
    @SerializedName("horaComida") val horaComida: String,
    @SerializedName("horaCena") val horaCena: String
)

data class MarcarComidaRequest(
    @SerializedName("tipoComida") val tipoComida: String,
    @SerializedName("estado") val estado: Boolean
)

data class EjercicioResponse(
    @SerializedName("kmRecorridosText") val kmRecorridosText: String,
    @SerializedName("metaKmText") val metaKmText: String,
    @SerializedName("porcentajeCumplimiento") val porcentajeCumplimiento: Int,
    @SerializedName("caloriasQuemadas") val caloriasQuemadas: Int,
    @SerializedName("rachaDias") val rachaDias: Int,
    @SerializedName("mensajeFaltanteText") val mensajeFaltanteText: String,
    @SerializedName("sugerenciaCaminataText") val sugerenciaCaminataText: String,
    @SerializedName("fraseMotivacional") val fraseMotivacional: String = ""
)

data class HabitosMetaEjercicioRequest(
    @SerializedName("nuevaMeta") val nuevaMeta: Double
)

data class RegistrarKmRequest(
    @SerializedName("km") val km: Double
)

data class FraseResponseDto(
    @SerializedName("idFrase") val idFrase: Int,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("texto") val texto: String
)

data class ProgresoPosturaResponseDto(
    @SerializedName("mensajeMeta") val mensajeMeta: String,
    @SerializedName("datosGrafica") val datosGrafica: List<ElementoGraficaPosturaDto>
)

data class ElementoGraficaPosturaDto(
    @SerializedName("diaSemana") val diaSemana: String,
    @SerializedName("totalAlertas") val totalAlertas: Int,
    @SerializedName("esHoy") val esHoy: Boolean
)

data class ErrorResponseDto(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("details") val details: String?
)
