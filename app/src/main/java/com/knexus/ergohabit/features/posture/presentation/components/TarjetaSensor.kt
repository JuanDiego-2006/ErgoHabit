package com.knexus.ergohabit.features.posture.presentation.components

import androidx.compose.foundation.background
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
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GreenLight),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Ícono teléfono — siempre verde, siempre activo
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(GreenPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PhoneAndroid,
                        contentDescription = null,
                        tint = BgWhite,
                        modifier = Modifier.size(24.dp)
                    )
                }

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
                        text = if (isSensorActive) "● Tiempo real · Monitoreando"
                        else "● Tiempo real · Postura OK",
                        fontSize = 12.sp,
                        color = GreenPrimary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${gradosInclinacion}°",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        text = "CORRECTO",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = GreenPrimary,
                        letterSpacing = 0.05.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Barra verde sólido
            LinearProgressIndicator(
                progress = { gradosInclinacion / 60f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = GreenPrimary,
                trackColor = BgWhite
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(text = "0° Ideal",    fontSize = 10.sp, color = TextGray,     modifier = Modifier.weight(1f))
                Text(text = "30° Límite",  fontSize = 10.sp, color = OrangeAccent, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text(text = "60° Crítico", fontSize = 10.sp, color = RedCritical,  modifier = Modifier.weight(1f), textAlign = TextAlign.End)
            }
        }
    }
}