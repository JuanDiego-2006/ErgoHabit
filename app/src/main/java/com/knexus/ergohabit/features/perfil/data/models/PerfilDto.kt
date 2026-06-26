package com.knexus.ergohabit.features.perfil.data.models

import com.google.gson.annotations.SerializedName

data class PerfilDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("primerApellido") val primerApellido: String,
    @SerializedName("segundoApellido") val segundoApellido: String,
    @SerializedName("email") val email: String,
    @SerializedName("idRol") val idRol: Int? = null,
    @SerializedName("nombreRol") val nombreRol: String? = null,
    @SerializedName("peso") val peso: Double,
    @SerializedName("estatura") val estatura: Double,
    @SerializedName("fotoUrl") val fotoUrl: String? = null
)

data class UpdatePerfilRequestDto(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("primerApellido") val primerApellido: String,
    @SerializedName("segundoApellido") val segundoApellido: String,
    @SerializedName("email") val email: String,
    @SerializedName("peso") val peso: Double,
    @SerializedName("estatura") val estatura: Double
)

data class FotoResponseDto(
    @SerializedName("mensaje") val mensaje: String,
    @SerializedName("url") val url: String
)
