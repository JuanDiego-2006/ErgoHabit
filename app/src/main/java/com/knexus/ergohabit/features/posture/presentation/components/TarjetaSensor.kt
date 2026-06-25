package com.knexus.ergohabit.features.posture.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knexus.ergohabit.ui.theme.*

@Composable
fun TarjetaSensor(
    isSensorActive: Boolean,
    gradosInclinacion: Int,
    onToggleClick: () -> Unit
) {
    val colorCirculo = if (isSensorActive) GreenPrimary else Color.Gray

    // Estados actualizados al nuevo límite de 25 grados
    val estadoTexto = when {
        !isSensorActive            -> "Inactivo"
        gradosInclinacion <= 25    -> "CORRECTO"
        gradosInclinacion <= 40    -> "ADVERTENCIA"
        else                       -> "CRÍTICO"
    }

    // Colores actualizados al nuevo límite de 25 grados
    val colorEstado = when {
        !isSensorActive            -> Color.Gray
        gradosInclinacion <= 25    -> GreenPrimary
        gradosInclinacion <= 40    -> OrangeAccent
        else                       -> RedCritical
    }

    // Progreso de la barra (0 a 60 grados -> 0% a 100%)
    val progreso = if (isSensorActive) (gradosInclinacion / 60f).coerceIn(0f, 1f) else 0f

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GreenLight),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleClick() } // Hacer la tarjeta clicable
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Ícono de teléfono
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(colorCirculo)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PhoneAndroid,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Textos principales
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = "Centinela Ergonómico",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = if (isSensorActive)
                            "• Tiempo real | ${if (gradosInclinacion <= 25) "Postura OK" else "Corrige tu postura"}"
                        else
                            "• Toca la tarjeta para activar",
                        fontSize = 12.sp,
                        color = if (isSensorActive) colorEstado else Color.Gray,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // Grados en tiempo real
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = if (isSensorActive) "${gradosInclinacion}°" else "--°",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isSensorActive) colorEstado else Color.Gray
                    )
                    Text(
                        text = estadoTexto,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorEstado,
                        letterSpacing = 0.05.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Barra de progreso
            LinearProgressIndicator(
                progress = { progreso },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = colorEstado,
                trackColor = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Textos base de la barra actualizados
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("0° Ideal",    fontSize = 10.sp, color = TextGray,     modifier = Modifier.weight(1f))
                Text("25° Límite",  fontSize = 10.sp, color = OrangeAccent, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text("60° Crítico", fontSize = 10.sp, color = RedCritical,  modifier = Modifier.weight(1f), textAlign = TextAlign.End)
            }
        }
    }
}