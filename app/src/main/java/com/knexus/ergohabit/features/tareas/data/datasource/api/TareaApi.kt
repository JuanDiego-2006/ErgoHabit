package com.knexus.ergohabit.features.tareas.data.datasource.api

import com.knexus.ergohabit.features.tareas.data.models.CategoriaDto
import com.knexus.ergohabit.features.tareas.data.models.TareaDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TareaApi {
    @GET("tareas/{idUsuario}")
    suspend fun getTareas(@Path("idUsuario") idUsuario: Int): List<TareaDto>

    @POST("tareas")
    suspend fun createTarea(@Body tarea: TareaDto): TareaDto

    @GET("tareas/mensaje-exito")
    suspend fun getMensajeExito(): Map<String, String>

    @POST("tareas/{idTarea}/completar")
    suspend fun completarTarea(@Path("idTarea") idTarea: Int): TareaDto

    @GET("categorias")
    suspend fun getCategorias(): List<CategoriaDto>
}
