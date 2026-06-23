package com.knexus.ergohabit.features.tareas.data.datasource.api

import com.knexus.ergohabit.features.tareas.data.models.TareaCreateRequestDto
import com.knexus.ergohabit.features.tareas.data.models.TareaDto
import com.knexus.ergohabit.features.tareas.data.models.TareasResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface TareaApi {
    @GET("api/v1/tareas")
    suspend fun getTareas(): TareasResponseDto

    @POST("api/v1/tareas")
    suspend fun createTarea(@Body tarea: TareaCreateRequestDto): TareaDto

    @PATCH("api/v1/tareas/{idTarea}/completar")
    suspend fun completarTarea(@Path("idTarea") idTarea: Int): TareaDto

    @GET("api/v1/tareas/mensaje-exito")
    suspend fun getMensajeExito(): Map<String, String>
}
