package com.knexus.ergohabit.features.posture.data.models

import com.google.gson.annotations.SerializedName


data class LoginRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("contrasena") val contrasena: String
)


data class LoginResponseDto(
    @SerializedName("token") val token: String
)


data class RegisterRequestDto(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("primerApellido") val primerApellido: String,
    @SerializedName("segundoApellido") val segundoApellido: String?,
    @SerializedName("email") val email: String,
    @SerializedName("contrasena") val contrasena: String,
    @SerializedName("idRol") val idRol: Int = 2
)


data class UserResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("primerApellido") val primerApellido: String,
    @SerializedName("segundoApellido") val segundoApellido: String?,
    @SerializedName("email") val email: String,
    @SerializedName("idRol") val idRol: Int
)

data class AuthErrorResponseDto(
    @SerializedName("error") val error: String
)
