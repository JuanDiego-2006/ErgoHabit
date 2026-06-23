package com.knexus.ergohabit.features.posture.domain.entities


data class Usuario(
    val id: Int,
    val nombre: String,
    val primerApellido: String,
    val segundoApellido: String?,
    val email: String,
    val idRol: Int
)
