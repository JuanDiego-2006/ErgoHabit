package com.knexus.ergohabit.features.posture.data.datasource.api

import com.knexus.ergohabit.features.posture.data.models.LoginRequestDto
import com.knexus.ergohabit.features.posture.data.models.LoginResponseDto
import com.knexus.ergohabit.features.posture.data.models.RegisterRequestDto
import com.knexus.ergohabit.features.posture.data.models.UserResponseDto
import retrofit2.http.Body
import retrofit2.http.POST


interface AuthApi {

    @POST("api/v1/auth/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): LoginResponseDto

    @POST("api/v1/auth/register")
    suspend fun register(
        @Body request: RegisterRequestDto
    ): UserResponseDto
}
