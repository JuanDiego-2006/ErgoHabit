package com.knexus.ergohabit.features.tareas.data.models

import com.google.gson.annotations.SerializedName

data class TareaDto(
    @SerializedName("id_tarea") val idTarea: Int,
    @SerializedName("id_usuario") val idUsuario: Int,
    @SerializedName("id_estado") val idEstado: Int,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("duracion_tarea") val duracionTarea: Int,
    @SerializedName("id_categoria") val idCategoria: Int,
    @SerializedName("fecha_creacion") val fechaCreacion: String
)
