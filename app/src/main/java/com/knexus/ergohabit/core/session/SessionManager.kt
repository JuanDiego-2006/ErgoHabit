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
    }

    /**
     * Guarda el token del usuario.
     */
    fun saveAuthToken(token: String) {
        prefs.edit().putString(USER_TOKEN, token).apply()
    }

    /**
     * Obtiene el token guardado.
     */
    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    /**
     * Elimina los datos de la sesión.
     */
    fun clearSession() {
        prefs.edit().remove(USER_TOKEN).apply()
    }
}
