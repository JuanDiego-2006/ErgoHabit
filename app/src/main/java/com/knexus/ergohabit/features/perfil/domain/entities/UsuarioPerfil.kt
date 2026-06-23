package com.knexus.ergohabit.features.perfil.domain.entities

data class UsuarioPerfil(
    val id: Int,
    val nombre: String,
    val primerApellido: String,
    val segundoApellido: String,
    val correo: String,
    val fotoUrl: String? = null
)
