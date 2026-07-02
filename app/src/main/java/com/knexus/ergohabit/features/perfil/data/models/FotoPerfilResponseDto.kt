package com.knexus.ergohabit.features.perfil.data.models

import com.google.gson.annotations.SerializedName

data class FotoPerfilResponseDto(
    @SerializedName("mensaje") val mensaje: String,
    @SerializedName("url") val url: String
)
