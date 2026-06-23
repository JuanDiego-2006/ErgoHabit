package com.knexus.ergohabit.features.tareas.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knexus.ergohabit.features.tareas.domain.entities.TareaEnfoque
import com.knexus.ergohabit.ui.theme.*

import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun CronometroSeccion(
    tarea: TareaEnfoque?,
    tiempoRestante: Int,
    isRunning: Boolean,
    onToggleTimer: () -> Unit
) {
    val totalSegundos = (tarea?.duracionMinutos ?: 45) * 60
    val progreso = if (totalSegundos > 0) tiempoRestante.toFloat() / totalSegundos else 0f

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        color = BgWhite,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título de la tarea y categoría
            if (tarea != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = when(tarea.categoria) {
                            "Académica" -> "🎓"
                            "Bienestar", "Salud" -> "🧘"
                            "Enfoque", "Trabajo" -> "🧠"
                            else -> "📌"
                        },
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = tarea.categoria,
                        style = MaterialTheme.typography.labelMedium,
                        color = TextGray,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = tarea.titulo,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            } else {
                Text(
                    text = "Selecciona una tarea",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextGray
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Círculo de progreso (las "rueditas")
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(200.dp)
            ) {
                // Círculo de fondo
                Canvas(modifier = Modifier.size(180.dp)) {
                    drawArc(
                        color = BgMain,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                
                // Círculo de progreso
                Canvas(modifier = Modifier.size(180.dp)) {
                    drawArc(
                        color = MoradoAcento,
                        startAngle = -90f,
                        sweepAngle = progreso * 360f,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "LISTO",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextGray,
                        letterSpacing = 1.sp
                    )
                    
                    val minutos = tiempoRestante / 60
                    val segundos = tiempoRestante % 60
                    val tiempoTexto = "%02d:%02d".format(minutos, segundos)
                    
                    Text(
                        text = tiempoTexto,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    
                    Text(
                        text = "minutos",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextGray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Sesión continua · ¡Mantén el foco!",
                style = MaterialTheme.typography.bodySmall,
                color = TextGray
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onToggleTimer,
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenPrimary
                ),
                shape = RoundedCornerShape(24.dp),
                enabled = tarea != null
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isRunning) "Pausar" else "Iniciar",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}
