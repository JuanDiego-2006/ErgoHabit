package com.knexus.ergohabit.features.perfil.data.datasource.api

import com.knexus.ergohabit.features.perfil.domain.entities.UsuarioPerfil
import retrofit2.http.GET
import retrofit2.http.Path

interface PerfilApi {
    @GET("api/v1/usuarios/{id}")
    suspend fun getPerfil(@Path("id") idUsuario: Int): UsuarioPerfil
}
