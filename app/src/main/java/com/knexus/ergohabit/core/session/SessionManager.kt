package com.knexus.ergohabit.core.session

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SessionManager @Inject constructor(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("ergo_session", Context.MODE_PRIVATE)

    companion object {
        private const val USER_TOKEN = "user_token"
    }


    fun saveAuthToken(token: String) {
        prefs.edit().putString(USER_TOKEN, token).apply()
    }


    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }


    fun fetchUserId(): Int {
        val token = fetchAuthToken() ?: return -1
        return try {
            val parts = token.split(".")
            if (parts.size < 2) return -1
            val payload = String(Base64.decode(parts[1], Base64.DEFAULT))
            val json = JSONObject(payload)
            json.getInt("idUsuario")
        } catch (e: Exception) {
            -1
        }
    }


    fun clearSession() {
        prefs.edit().remove(USER_TOKEN).apply()
    }
}
