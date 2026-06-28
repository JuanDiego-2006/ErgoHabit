package com.knexus.ergohabit.core.network

import com.google.gson.Gson
import com.knexus.ergohabit.features.tareas.data.models.TareaErrorDto
import retrofit2.HttpException

object ApiErrorParser {

    private val gson = Gson()

    fun mensaje(error: Throwable): String {
        if (error is HttpException) {
            val body = error.response()?.errorBody()?.string()
            if (!body.isNullOrBlank()) {
                runCatching {
                    gson.fromJson(body, TareaErrorDto::class.java).message
                }.getOrNull()?.takeIf { it.isNotBlank() }?.let { return it }

                runCatching {
                    gson.fromJson(body, Map::class.java)["message"]?.toString()
                }.getOrNull()?.takeIf { it.isNotBlank() }?.let { return it }
            }
            return when (error.code()) {
                400 -> "Datos inválidos. Revisa título, categoría y duración (mínimo 20 min)."
                401 -> "Sesión expirada. Vuelve a iniciar sesión."
                403 -> "No tienes permiso para esta acción."
                else -> "Error del servidor (${error.code()})"
            }
        }
        return error.message ?: "Error desconocido"
    }
}
