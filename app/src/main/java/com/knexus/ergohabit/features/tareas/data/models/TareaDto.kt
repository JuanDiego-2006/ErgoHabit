package com.knexus.ergohabit.features.tareas.data.models

import com.google.gson.annotations.SerializedName

/**
 * DTO para recibir la lista agrupada de tareas desde la API.
 */
data class TareasResponseDto(
    @SerializedName("totalPendientesText") val totalPendientesText: String,
    @SerializedName("totalCompletadasText") val totalCompletadasText: String,
    @SerializedName("pendientes") val pendientes: List<TareaDto>,
    @SerializedName("completadas") val completadas: List<TareaDto>
)

/**
 * DTO que representa una tarea individual en la respuesta de la API.
 */
data class TareaDto(
    @SerializedName("idTarea") val idTarea: Int,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("duracionText") val duracionText: String,
    @SerializedName("idEstado") val idEstado: Int
)

/**
 * DTO para enviar la solicitud de creación de una nueva tarea.
 */
data class TareaCreateRequestDto(
    @SerializedName("titulo") val titulo: String,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("duracionTarea") val duracionTarea: Int
)
