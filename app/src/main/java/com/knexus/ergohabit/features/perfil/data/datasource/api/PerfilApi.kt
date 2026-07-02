package com.knexus.ergohabit.features.perfil.data.datasource.api

import com.knexus.ergohabit.features.perfil.data.models.FotoPerfilResponseDto
import com.knexus.ergohabit.features.perfil.domain.entities.UsuarioPerfil
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part
import retrofit2.http.Path

interface PerfilApi {
    @GET("api/v1/usuarios/{id}")
    suspend fun getPerfil(@Path("id") idUsuario: Int): UsuarioPerfil

    @Multipart
    @PATCH("api/v1/usuarios/perfil/foto")
    suspend fun updateFotoPerfil(
        @Part file: MultipartBody.Part
    ): FotoPerfilResponseDto
}
