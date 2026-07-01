package com.knexus.ergohabit.features.posture.data.datasource.api

// Importamos todas las clases del paquete donde viven tus DTOs
import com.knexus.ergohabit.features.posture.data.models.*
import retrofit2.http.*

interface HabitosApi {

    // --- SUEÑO ---
    @GET("api/v1/habitos/sueno/dashboard")
    suspend fun obtenerDashboardSueno(): SuenoResponse


    @PUT("api/v1/habitos/sueno/horario")
    suspend fun configurarHorarioSueno(@Body request: SuenoRequest): MensajeResponse

    @POST("api/v1/habitos/sueno/despertar")
    suspend fun registrarDespertarSueno(): MensajeResponse

    // --- NUTRICIÓN ---
    @GET("api/v1/habitos/nutricion/dashboard")
    suspend fun obtenerDashboardNutricion(): NutricionDashboardResponse

    @PUT("api/v1/habitos/nutricion/horarios")
    suspend fun configurarHorariosNutricion(@Body request: ConfigurarNutricionRequest): MensajeResponse

    @POST("api/v1/habitos/nutricion/marcar")
    suspend fun marcarComida(@Body request: MarcarComidaRequest): MensajeResponse

    // --- EJERCICIO ---
    @GET("api/v1/habitos/ejercicio/dashboard")
    suspend fun obtenerDashboardEjercicio(): EjercicioResponse



    @PUT("api/v1/habitos/ejercicio/meta")
    suspend fun configurarMetaEjercicio(@Body request: HabitosMetaEjercicioRequest): MensajeResponse

    @POST("api/v1/habitos/ejercicio/recorrido")
    suspend fun registrarKilometros(@Body request: RegistrarKmRequest): MensajeResponse

    // --- FRASES ---
    @GET("api/v1/frases/aleatoria/{categoria}")
    suspend fun obtenerFraseAleatoria(@Path("categoria") categoria: String): FraseResponseDto


}