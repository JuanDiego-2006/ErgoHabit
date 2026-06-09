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

// Solo Hábitos hoy y Racha — sin Puntos
@Composable
fun FilaEstadisticas(vibraciones: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        TarjetaEstadistica(
            valor = "1/4",
            label = "Hábitos hoy",
            colorFondo = BgWhite,
            colorTexto = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        TarjetaEstadistica(
            valor = "12 días",
            label = "Racha",
            colorFondo = PurpleLight,
            colorTexto = PurpleAccent,
            modifier = Modifier.weight(1f)
        )
    }
}

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