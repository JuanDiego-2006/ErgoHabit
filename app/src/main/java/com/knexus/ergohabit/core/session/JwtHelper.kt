package com.knexus.ergohabit.core.session

import android.util.Base64
import org.json.JSONObject

object JwtHelper {

    fun extractUserId(token: String): Int? {
        return extractClaim(token, "idUsuario")?.toIntOrNull()
    }

    fun extractRoleId(token: String): Int? {
        return extractClaim(token, "idRol")?.toIntOrNull()
    }

    private fun extractClaim(token: String, claim: String): String? {
        return try {
            val payload = token.split(".").getOrNull(1) ?: return null
            val decoded = Base64.decode(payload, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
            val json = JSONObject(String(decoded, Charsets.UTF_8))
            when {
                json.has(claim) && !json.isNull(claim) -> json.get(claim).toString()
                else -> null
            }
        } catch (_: Exception) {
            null
        }
    }
}
