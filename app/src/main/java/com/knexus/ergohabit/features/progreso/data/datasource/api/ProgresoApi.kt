package com.knexus.ergohabit.features.progreso.data.datasource.api

import com.knexus.ergohabit.features.progreso.data.models.DetalleHabitoDto
import com.knexus.ergohabit.features.progreso.data.models.FraseDto
import com.knexus.ergohabit.features.progreso.data.models.HabitoProgresoDto
import com.knexus.ergohabit.features.progreso.data.models.TendenciaGeneralDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ProgresoApi {
    @GET("api/v1/progreso/habitos/{idUsuario}")
    suspend fun getHabitosProgreso(@Path("idUsuario") idUsuario: Int): List<HabitoProgresoDto>

    @GET("api/v1/progreso/tendencia/{idUsuario}")
    suspend fun getTendenciaGeneral(@Path("idUsuario") idUsuario: Int): TendenciaGeneralDto

    @GET("api/v1/progreso/detalle/{idUsuario}/{idHabito}")
    suspend fun getDetalleHabito(
        @Path("idUsuario") idUsuario: Int,
        @Path("idHabito") idHabito: Int
    ): DetalleHabitoDto

    @GET("api/v1/frases/aleatoria")
    suspend fun getFraseAleatoria(): FraseDto

    @GET("api/v1/habitos/sueno/progreso-semanal")
    suspend fun getProgresoSemanalSueno(): com.knexus.ergohabit.features.progreso.data.models.HabitoProgresoSemanalDto

    @GET("api/v1/habitos/agua/progreso-semanal")
    suspend fun getProgresoSemanalAgua(): com.knexus.ergohabit.features.progreso.data.models.HabitoProgresoSemanalDto

    @GET("api/v1/habitos/ejercicio/progreso-semanal")
    suspend fun getProgresoSemanalEjercicio(): com.knexus.ergohabit.features.progreso.data.models.HabitoProgresoSemanalDto

    @GET("api/v1/ergonomia/progreso-semanal")
    suspend fun getProgresoSemanalPostura(): com.knexus.ergohabit.features.progreso.data.models.HabitoProgresoSemanalDto
}
