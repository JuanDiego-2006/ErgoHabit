package com.knexus.ergohabit.features.tareas.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knexus.ergohabit.ui.theme.*
import kotlin.math.roundToInt

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign

@Composable
fun SheetMasTiempo(
    onConfirmar: (Int) -> Unit,
    onCancelar: () -> Unit
) {
    var minutosExtra by remember { mutableStateOf(30) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "💪", fontSize = 48.sp)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "¡No te rindas! Eres capaz de lograrlo",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        
        Text(text = "⚡", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "¿Cuánto tiempo más necesitas?",
            style = MaterialTheme.typography.bodyMedium,
            color = TextGray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Selector con - y +
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(BgMain)
                    .clickable { if (minutosExtra > 15) minutosExtra -= 15 }, // Cambiado a saltos de 15 min para facilitar horas
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Remove, contentDescription = null, tint = TextGray)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                val hrs = minutosExtra / 60
                val mins = minutosExtra % 60
                
                Text(
                    text = when {
                        hrs > 0 && mins > 0 -> "$hrs hr $mins"
                        hrs > 0 -> "$hrs"
                        else -> "$minutosExtra"
                    },
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = when {
                        hrs > 0 && mins > 0 -> "horas y minutos"
                        hrs > 0 -> if (hrs == 1) "hora adicional" else "horas adicionales"
                        else -> "minutos adicionales"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = TextGray
                )
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(BgMain)
                    .clickable { if (minutosExtra < 120) minutosExtra += 15 }, // Saltos de 15 min
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = TextGray)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Barra de progreso/Slider
        LinearProgressIndicator(
            progress = { minutosExtra.toFloat() / 120f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = GreenPrimary,
            trackColor = BgMain
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("15 min", style = MaterialTheme.typography.labelSmall, color = TextGray)
            Text("Máximo: 2 horas", style = MaterialTheme.typography.labelSmall, color = TextGray)
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Botones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onCancelar,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BgMain),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("× Cancelar", color = TextGray, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { onConfirmar(minutosExtra) },
                modifier = Modifier
                    .weight(1.5f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                shape = RoundedCornerShape(16.dp)
            ) {
                val hrs = minutosExtra / 60
                val mins = minutosExtra % 60
                val textoBoton = when {
                    hrs > 0 && mins > 0 -> "$hrs hr $mins min"
                    hrs > 0 -> "$hrs hr"
                    else -> "$minutosExtra min"
                }
                Text("✓ Agregar $textoBoton", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
