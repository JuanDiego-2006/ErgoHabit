package com.knexus.ergohabit.features.posture.data.datasource.api

import com.knexus.ergohabit.features.posture.data.models.*
import retrofit2.http.*

interface AguaApi {

    @GET("api/v1/habitos/agua/dashboard")
    suspend fun obtenerDashboardAgua(): DashboardAguaResponse

    @POST("api/v1/habitos/agua/toma")
    suspend fun registrarToma(
        @Body request: RegistrarTomaRequest
    ): AguaMensajeResponse

    @PUT("api/v1/habitos/agua/meta")
    suspend fun configurarMeta(
        @Body request: ConfigurarMetaRequest
    ): AguaMensajeResponse

    @GET("api/v1/habitos/agua/progreso-semanal")
    suspend fun obtenerProgresoSemanalAgua(): HistorialHabitoResponse
}
