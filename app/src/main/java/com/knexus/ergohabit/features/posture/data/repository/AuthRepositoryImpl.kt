package com.knexus.ergohabit.features.posture.data.repository

import com.google.gson.Gson
import com.knexus.ergohabit.features.posture.data.datasource.api.AuthApi
import com.knexus.ergohabit.features.posture.data.models.AuthErrorResponseDto
import com.knexus.ergohabit.features.posture.data.models.LoginRequestDto
import com.knexus.ergohabit.features.posture.data.models.RegisterRequestDto
import com.knexus.ergohabit.features.posture.domain.entities.Sesion
import com.knexus.ergohabit.features.posture.domain.entities.Usuario
import com.knexus.ergohabit.features.posture.domain.repository.AuthRepository
import retrofit2.HttpException
import javax.inject.Inject


class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi
) : AuthRepository {

    private val gson = Gson()

    override suspend fun login(email: String, password: String): Result<Sesion> {
        return try {
            val request = LoginRequestDto(email, password)
            val response = api.login(request)
            Result.success(Sesion(token = response.token))
        } catch (e: HttpException) {
            Result.failure(Exception(parseError(e)))
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
        } catch (e: HttpException) {
            Result.failure(Exception(parseError(e)))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseError(e: HttpException): String {
        return try {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = gson.fromJson(errorBody, AuthErrorResponseDto::class.java)
            errorResponse.error
        } catch (ex: Exception) {
            "Error de red o del servidor"
        }
    }
}
