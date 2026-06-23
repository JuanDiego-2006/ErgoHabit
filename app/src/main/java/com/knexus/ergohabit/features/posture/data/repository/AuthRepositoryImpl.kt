package com.knexus.ergohabit.features.posture.data.repository

import com.knexus.ergohabit.features.posture.data.datasource.api.AuthApi
import com.knexus.ergohabit.features.posture.data.models.LoginRequestDto
import com.knexus.ergohabit.features.posture.data.models.RegisterRequestDto
import com.knexus.ergohabit.features.posture.domain.entities.Sesion
import com.knexus.ergohabit.features.posture.domain.entities.Usuario
import com.knexus.ergohabit.features.posture.domain.repository.AuthRepository
import javax.inject.Inject


class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<Sesion> {
        return try {
            val request = LoginRequestDto(email, password)
            val response = api.login(request)
            Result.success(Sesion(token = response.token))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(
        nombre: String,
        primerApellido: String,
        segundoApellido: String?,
        email: String,
        contrasena: String
    ): Result<Usuario> {
        return try {
            val request = RegisterRequestDto(
                nombre = nombre,
                primerApellido = primerApellido,
                segundoApellido = segundoApellido,
                email = email,
                contrasena = contrasena
            )
            val response = api.register(request)
            Result.success(
                Usuario(
                    id = response.id,
                    nombre = response.nombre,
                    primerApellido = response.primerApellido,
                    segundoApellido = response.segundoApellido,
                    email = response.email,
                    idRol = response.idRol
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
