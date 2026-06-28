package com.knexus.ergohabit.core.hardware.domain

import com.google.mlkit.vision.face.Face
import com.knexus.ergohabit.core.hardware.domain.model.ResultadoPosturaCamara
import kotlin.math.abs

/**
 * Evalúa postura solo con el rostro (funciona con el teléfono cerca, sin ver hombros).
 */
object EvaluadorPosturaRostro {

    fun sinRostro(): ResultadoPosturaCamara = ResultadoPosturaCamara(
        personaDetectada = false,
        esCorrecta = true,
        motivo = "Sin rostro visible"
    )

    fun evaluar(cara: Face): ResultadoPosturaCamara {
        val pitch = cara.headEulerAngleX
        val yaw = cara.headEulerAngleY
        val roll = cara.headEulerAngleZ

        if (abs(yaw) >= 20f) {
            return alerta("Posición lateral detectada")
        }
        if (abs(roll) >= 16f) {
            return alerta("Cabeza inclinada — mala ergonomía")
        }
        if (pitch >= 30f) {
            return alerta("Cuello inclinado hacia adelante")
        }
        if (pitch <= -22f) {
            return alerta("Cabeza reclinada — posible postura acostada")
        }

        return ResultadoPosturaCamara(
            personaDetectada = true,
            esCorrecta = true,
            motivo = "Rostro en posición correcta"
        )
    }

    private fun alerta(motivo: String) = ResultadoPosturaCamara(
        personaDetectada = true,
        esCorrecta = false,
        motivo = motivo
    )
}
