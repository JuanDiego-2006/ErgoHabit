package com.knexus.ergohabit.features.tareas.data.datasource.api

import com.knexus.ergohabit.features.tareas.data.models.MessageResponseDto
import com.knexus.ergohabit.features.tareas.data.models.TareaCreateRequestDto
import com.knexus.ergohabit.features.tareas.data.models.TareaDto
import com.knexus.ergohabit.features.tareas.data.models.TareasResponseDto
import retrofit2.http.*

interface TareaApi {
    @GET("api/v1/tareas")
    suspend fun getTareas(): TareasResponseDto

    @POST("api/v1/tareas")
    suspend fun createTarea(@Body tarea: TareaCreateRequestDto): MessageResponseDto

    @PATCH("api/v1/tareas/{idTarea}/iniciar")
    suspend fun iniciarTarea(@Path("idTarea") idTarea: Int): MessageResponseDto

    @PATCH("api/v1/tareas/{idTarea}/pausar")
    suspend fun pausarTarea(@Path("idTarea") idTarea: Int): MessageResponseDto

    @PATCH("api/v1/tareas/{idTarea}/completar")
    suspend fun completarTarea(@Path("idTarea") idTarea: Int): MessageResponseDto

    @PATCH("api/v1/tareas/{idTarea}/extender")
    suspend fun extenderTarea(
        @Path("idTarea") idTarea: Int,
        @Body request: com.knexus.ergohabit.features.tareas.data.models.TareaExtenderRequestDto
    ): MessageResponseDto

    @DELETE("api/v1/tareas/{idTarea}")
    suspend fun eliminarTarea(@Path("idTarea") idTarea: Int): MessageResponseDto

    @GET("api/v1/tareas/{idTarea}/cronometro")
    suspend fun getInfoCronometro(@Path("idTarea") idTarea: Int): com.knexus.ergohabit.features.tareas.data.models.CronometroAlertaDto
}
