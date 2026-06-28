package com.knexus.ergohabit.features.posture.data.datasource.api

import com.knexus.ergohabit.features.posture.data.models.PosturaDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface PosturaApi {

    @GET("postura/{idUsuario}")
    suspend fun obtenerPostura(
        @Path("idUsuario") idUsuario: Int
    ): PosturaDto

    @POST("postura/registrar")
    suspend fun registrarPostura(
        @Body postura: PosturaDto
    ): PosturaDto
}
