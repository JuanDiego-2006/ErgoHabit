package com.knexus.ergohabit.features.tareas.data.models

import com.google.gson.annotations.SerializedName

data class CategoriaDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("icono") val icono: String? = null
)
