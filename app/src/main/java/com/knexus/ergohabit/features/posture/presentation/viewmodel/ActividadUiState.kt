package com.knexus.ergohabit.features.posture.presentation.viewmodel

data class ActividadUiState(
    val kmActuales: Float = 0f,
    val kmObjetivo: Float = 8f,
    val calorias: Int = 0,
    val rachasDias: Int = 0,
    val mensajeFaltante: String = "",
    val sugerencia: String = "",
    val fraseMotivacional: String = "",
    val porcentajeBackend: Int = 0,
    val isLoading: Boolean = true,
    val isRegistrando: Boolean = false,
    val sensorDisponible: Boolean = true,
    val sensorActivo: Boolean = false,
    val pasosSesion: Int = 0,
    val kmSesion: Float = 0f,
    val errorSensor: String? = null
) {
    val porcentaje: Float
        get() = if (kmObjetivo > 0) {
            (kmActuales / kmObjetivo).coerceIn(0f, 1f)
        } else {
            0f
        }
    val kmRestantes: Float get() = (kmObjetivo - kmActuales).coerceAtLeast(0f)
    val metaCumplida: Boolean get() = kmObjetivo > 0f && kmActuales >= kmObjetivo
    val mensajeBanner: String
        get() = if (metaCumplida) {
            mensajeFaltante.ifBlank { "¡Meta diaria alcanzada!" }
        } else {
            "¡Te faltan ${"%.2f".format(kmRestantes)} km!"
        }
    val sugerenciaBanner: String
        get() = if (metaCumplida) {
            sugerencia.ifBlank { "¡Excelente trabajo! Has cumplido tu objetivo de hoy." }
        } else {
            sugerencia.ifBlank {
                "Una caminata de ${(kmRestantes * 10).toInt().coerceAtLeast(5)} minutos te acercará a tu meta 🚶"
            }
        }
}
