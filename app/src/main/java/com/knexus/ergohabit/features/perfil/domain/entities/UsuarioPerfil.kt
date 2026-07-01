package com.knexus.ergohabit.features.perfil.domain.entities

import com.google.gson.annotations.SerializedName

data class UsuarioPerfil(
    val id: Int,
    val nombre: String,
    val primerApellido: String,
    val segundoApellido: String,
    val correo: String,
    val idRol: Int,
    val peso: Double,
    val estatura: Double,
    val fotoUrl: String? = null

)
