package com.knexus.ergohabit.features.progreso.data.datasource.api

import com.knexus.ergohabit.features.progreso.data.models.ProgresoDiarioDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ProgresoDiarioApi {
    @GET("api/v1/progresodiario/{idUsuario}")
    suspend fun getProgresoDiario(@Path("idUsuario") idUsuario: Int): ProgresoDiarioDto
}
