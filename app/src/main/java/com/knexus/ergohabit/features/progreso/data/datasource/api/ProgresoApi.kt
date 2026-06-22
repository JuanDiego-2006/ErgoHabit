package com.knexus.ergohabit.features.progreso.data.datasource.api

import com.knexus.ergohabit.features.progreso.data.models.DetalleHabitoDto
import com.knexus.ergohabit.features.progreso.data.models.HabitoProgresoDto
import com.knexus.ergohabit.features.progreso.data.models.TendenciaGeneralDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ProgresoApi {
    @GET("progreso/habitos/{idUsuario}")
    suspend fun getHabitosProgreso(@Path("idUsuario") idUsuario: Int): List<HabitoProgresoDto>

    @GET("progreso/tendencia/{idUsuario}")
    suspend fun getTendenciaGeneral(@Path("idUsuario") idUsuario: Int): TendenciaGeneralDto

    @GET("progreso/detalle/{idUsuario}/{idHabito}")
    suspend fun getDetalleHabito(
        @Path("idUsuario") idUsuario: Int,
        @Path("idHabito") idHabito: Int
    ): DetalleHabitoDto
}
