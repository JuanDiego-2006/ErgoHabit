package com.knexus.ergohabit.features.perfil.data.datasource.api

import com.knexus.ergohabit.features.perfil.data.models.PerfilDto
import com.knexus.ergohabit.features.perfil.data.models.UpdatePerfilRequestDto
import com.knexus.ergohabit.features.perfil.data.models.FotoResponseDto
import okhttp3.MultipartBody
import retrofit2.http.*

interface PerfilApi {
    @GET("api/v1/usuarios/{id}")
    suspend fun getPerfil(@Path("id") idUsuario: Int): PerfilDto

    @PUT("api/v1/usuarios/{id}")
    suspend fun updatePerfil(
        @Path("id") idUsuario: Int,
        @Body request: UpdatePerfilRequestDto
    ): PerfilDto

    @Multipart
    @PATCH("api/v1/usuarios/perfil/foto")
    suspend fun uploadFoto(
        @Part foto: MultipartBody.Part
    ): FotoResponseDto
}
