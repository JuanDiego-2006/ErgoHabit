package com.knexus.ergohabit.features.posture.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knexus.ergohabit.ui.theme.*

/**
 * Fila de tarjetas de estadísticas rápidas.
 */
@Composable
fun FilaEstadisticas(vibraciones: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        TarjetaEstadistica(valor = "1/4", label = "Hábitos hoy", colorFondo = BlancoPuro, colorTexto = TextoPrimario, modifier = Modifier.weight(1f))
        TarjetaEstadistica(valor = "$vibraciones", label = "Alertas", colorFondo = MoradoClaro, colorTexto = MoradoAcento, modifier = Modifier.weight(1f))
        TarjetaEstadistica(valor = "340 pts", label = "Puntos", colorFondo = NaranjaClaro, colorTexto = NaranjaAcento, modifier = Modifier.weight(1f))
    }
}

/**
 * Componente base para cada tarjeta de estadística.
 */
@Composable
fun TarjetaEstadistica(
    valor: String,
    label: String,
    colorFondo: Color,
    colorTexto: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth()
        ) {
            Text(text = valor, fontSize = 20.sp, fontWeight = FontWeight.Black, color = colorTexto)
            Text(text = label, fontSize = 11.sp, color = colorTexto, modifier = Modifier.padding(top = 2.dp))
        }
    }
}
