package com.knexus.ergohabit.features.perfil.domain.entities

import com.google.gson.annotations.SerializedName

data class UsuarioPerfil(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("primerApellido") val primerApellido: String,
    @SerializedName("segundoApellido") val segundoApellido: String,
    @SerializedName("email") val correo: String,
    @SerializedName("fotoUrl") val fotoUrl: String? = null
)
