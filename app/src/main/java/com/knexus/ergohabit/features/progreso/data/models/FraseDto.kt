package com.knexus.ergohabit.features.progreso.data.models

import com.google.gson.annotations.SerializedName


data class FraseDto(
    @SerializedName("idFrase") val idFrase: Int,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("texto") val texto: String
)
