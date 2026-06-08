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
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GreenLight),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleClick() } // El clic se delega a la pantalla principal
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (isSensorActive) GreenPrimary else Color.Gray)
                ) {
                    Icon(Icons.Outlined.PhoneAndroid, contentDescription = null, tint = BgWhite, modifier = Modifier.size(24.dp))
                }
                Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                    Text("Centinela Ergonómico", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(
                        text = if (isSensorActive) "● Tiempo real · Monitoreando" else "○ Inactivo · Toca para activar",
                        fontSize = 12.sp, color = if (isSensorActive) GreenPrimary else Color.Gray, modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(if(isSensorActive) "$gradosInclinacion°" else "--°", fontSize = 32.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                    Text(if(isSensorActive) "CORRECTO" else "PAUSADO", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = if(isSensorActive) GreenPrimary else Color.Gray, letterSpacing = 0.05.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            LinearProgressIndicator(
                progress = { if(isSensorActive) 0.37f else 0f },
                modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(6.dp)),
                color = GreenPrimary, trackColor = BgWhite
            )

            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("0° Ideal", fontSize = 10.sp, color = TextGray, modifier = Modifier.weight(1f))
                Text("30° Límite", fontSize = 10.sp, color = OrangeAccent, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text("60° Crítico", fontSize = 10.sp, color = RedCritical, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
            }
        }
    }
}