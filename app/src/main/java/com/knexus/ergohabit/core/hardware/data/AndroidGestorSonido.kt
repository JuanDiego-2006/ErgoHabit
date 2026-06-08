package com.knexus.ergohabit.core.hardware.data

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.knexus.ergohabit.core.hardware.domain.GestorSonido
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Implementación de Android para la gestión de sonidos y vibraciones.
 */
class AndroidGestorSonido @Inject constructor(
    @ApplicationContext private val contexto: Context
) : GestorSonido {

    private val generadorTonos = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
    private val vibrador = contexto.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    override fun sonarAlerta() {
        // Sonar un pitido de alerta
        generadorTonos.startTone(ToneGenerator.TONE_CDMA_PIP, 150)
        
        // Vibrar durante 500ms con compatibilidad para versiones antiguas (API 24/25)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrador.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            // Método antiguo para versiones anteriores a Android Oreo
            @Suppress("DEPRECATION")
            vibrador.vibrate(500)
        }
    }

    override fun detenerSonido() {
        generadorTonos.stopTone()
        vibrador.cancel()
    }
}
