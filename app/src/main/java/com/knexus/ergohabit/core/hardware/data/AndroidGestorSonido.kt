package com.knexus.ergohabit.core.hardware.data

import android.annotation.SuppressLint
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

    @SuppressLint("MissingPermission")
    override fun sonarAlerta() {
        // Sonar un pitido de alerta
        generadorTonos.startTone(ToneGenerator.TONE_CDMA_PIP, 200)
        
        // Patrón de vibración elegante (el que configuramos antes)
        val pattern = longArrayOf(0, 500, 2000, 500)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrador.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrador.vibrate(pattern, -1)
        }
    }

    @SuppressLint("MissingPermission")
    override fun detenerSonido() {
        generadorTonos.stopTone()
        vibrador.cancel()
    }
}
