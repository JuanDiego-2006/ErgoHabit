package com.knexus.ergohabit.features.posture.domain.repository

import com.knexus.ergohabit.features.posture.domain.entities.Sesion
import com.knexus.ergohabit.features.posture.domain.entities.Usuario


interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Sesion>
    
    suspend fun register(
        nombre: String,
        primerApellido: String,
        segundoApellido: String?,
        email: String,
        contrasena: String
    ): Result<Usuario>
}
