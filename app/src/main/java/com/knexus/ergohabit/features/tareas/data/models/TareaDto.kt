package com.knexus.ergohabit.features.tareas.data.models

import com.google.gson.annotations.SerializedName


data class TareasResponseDto(
    @SerializedName("totalPendientesText") val totalPendientesText: String? = null,
    @SerializedName("totalCompletadasText") val totalCompletadasText: String? = null,
    @SerializedName("pendientes") val pendientes: List<TareaDto>? = null,
    @SerializedName("completadas") val completadas: List<TareaDto>? = null
)


data class TareaDto(
    @SerializedName("idTarea") val idTarea: Int? = null,
    @SerializedName("titulo") val titulo: String? = null,
    @SerializedName("categoria") val categoria: String? = null,
    @SerializedName("duracionText") val duracionText: String? = null,
    @SerializedName("idEstado") val idEstado: Int? = null,
    @SerializedName("fechaInicioCronometro") val fechaInicioCronometro: String? = null
)

data class MessageResponseDto(
    @SerializedName("mensaje") val mensaje: String? = null,
    @SerializedName("message") val errorMensaje: String? = null,
    @SerializedName("code") val codigo: String? = null
)


data class TareaCreateRequestDto(
    @SerializedName("titulo") val titulo: String,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("duracionTarea") val duracionTarea: Int
)

data class TareaExtenderRequestDto(
    @SerializedName("minutosExtra") val minutosExtra: Int
)
