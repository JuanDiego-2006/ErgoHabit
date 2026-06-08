package com.knexus.ergohabit.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val EsquemaOscuro = darkColorScheme(
    primary = VerdePrimario,
    secondary = MoradoAcento,
    tertiary = NaranjaAcento,
    background = TextoPrimario,
    surface = TextoPrimario,
    onPrimary = BlancoPuro,
    onSecondary = BlancoPuro,
    onTertiary = BlancoPuro,
    onBackground = BlancoPuro,
    onSurface = BlancoPuro
)

private val EsquemaClaro = lightColorScheme(
    primary = VerdePrimario,
    secondary = MoradoAcento,
    tertiary = NaranjaAcento,
    background = FondoPrincipal,
    surface = BlancoPuro,
    onPrimary = BlancoPuro,
    onSecondary = BlancoPuro,
    onTertiary = BlancoPuro,
    onBackground = TextoPrimario,
    onSurface = TextoPrimario
)

@Composable
fun ErgoHabitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Colores dinámicos disponibles en Android 12+
    dynamicColor: Boolean = false, // Lo ponemos en false para mantener tu identidad visual
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> EsquemaOscuro
        else -> EsquemaClaro
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
