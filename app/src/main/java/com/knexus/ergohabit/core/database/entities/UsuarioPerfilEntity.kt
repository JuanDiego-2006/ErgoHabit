package com.knexus.ergohabit.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuario_perfil")
data class UsuarioPerfilEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    val primerApellido: String,
    val segundoApellido: String,
    val correo: String,
    val idRol: Int,
    val peso: Double,
    val estatura: Double,
    val fotoUrl: String? = null
)
