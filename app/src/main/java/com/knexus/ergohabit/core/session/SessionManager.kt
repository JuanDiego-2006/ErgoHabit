package com.knexus.ergohabit.core.session

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gestor de sesión para almacenar el token de autenticación de forma persistente.
 */
@Singleton
class SessionManager @Inject constructor(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("ergo_session", Context.MODE_PRIVATE)

    companion object {
        private const val USER_TOKEN = "user_token"
        private const val USER_ID = "user_id"
    }

    /**
     * Guarda el token del usuario.
     */
    fun saveAuthToken(token: String) {
        val userId = JwtHelper.extractUserId(token)
        prefs.edit()
            .putString(USER_TOKEN, token)
            .apply {
                if (userId != null) putInt(USER_ID, userId)
            }
            .apply()
    }

    /**
     * Obtiene el token guardado.
     */
    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    /**
     * Obtiene el ID del usuario autenticado desde el JWT o preferencias.
     */
    fun fetchUserId(): Int? {
        val stored = prefs.getInt(USER_ID, -1)
        if (stored > 0) return stored
        return fetchAuthToken()?.let { JwtHelper.extractUserId(it) }
    }

    /**
     * Elimina los datos de la sesión.
     */
    /**
     * Guarda el progreso en segundos de una tarea específica.
     */
    fun saveTaskProgress(idTarea: Int, segundosRestantes: Int) {
        prefs.edit().putInt("task_progress_$idTarea", segundosRestantes).apply()
    }

    /**
     * Recupera el progreso en segundos de una tarea.
     */
    fun getTaskProgress(idTarea: Int): Int {
        return prefs.getInt("task_progress_$idTarea", -1)
    }

    /**
     * Elimina el progreso guardado de una tarea (ej. al completarla).
     */
    fun clearTaskProgress(idTarea: Int) {
        prefs.edit().remove("task_progress_$idTarea").apply()
    }

    fun clearSession() {
        prefs.edit()
            .remove(USER_TOKEN)
            .remove(USER_ID)
            .apply()
    }
}
