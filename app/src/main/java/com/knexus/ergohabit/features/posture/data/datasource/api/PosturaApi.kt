package com.knexus.ergohabit.features.posture.data.datasource.api

import com.knexus.ergohabit.features.posture.data.models.PosturaDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Interfaz de Retrofit para las peticiones de postura al servidor.
 */
interface PosturaApi {

    @GET("api/v1/ergonomia/historial")
    suspend fun obtenerHistorial(): List<PosturaDto>

    @GET("api/v1/ergonomia/progreso-semanal")
    suspend fun obtenerProgresoSemanal(): com.knexus.ergohabit.features.posture.data.models.ProgresoPosturaResponseDto

    @POST("api/v1/ergonomia/sincronizar")
    suspend fun sincronizarAlertas(
        @Body request: RegistroPosturaRequest
    ): com.knexus.ergohabit.features.posture.data.models.MensajeResponse
}

data class RegistroPosturaRequest(
    val totalAlertas: Int
)
