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
    duracionSesionActual: Int,
    isRunning: Boolean,
    onToggleTimer: () -> Unit
) {
    val totalSegundos = if (duracionSesionActual > 0) duracionSesionActual else (tarea?.duracionMinutos ?: 45) * 60
    val progreso = if (totalSegundos > 0) tiempoRestante.toFloat() / totalSegundos else 0f
    
    val totalMinutos = tiempoRestante / 60
    val horas = totalMinutos / 60
    val minsRestantes = totalMinutos % 60
    val segundos = tiempoRestante % 60
    
    // Ajuste dinámico de tamaño para evitar amontonamiento cuando hay horas
    val containerSize = if (horas > 0) 240.dp else 210.dp
    val canvasSize = if (horas > 0) 220.dp else 190.dp

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
            if (tarea != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = when(tarea.categoria) {
                            "Académica" -> "📚"
                            "Laboral" -> "💼"
                            "Bienestar" -> "🧘"
                            "Enfoque Profundo" -> "🧠"
                            "Personal" -> "👤"
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
            
            // Círculo de progreso con tamaño dinámico
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(containerSize)
            ) {
                Canvas(modifier = Modifier.size(canvasSize)) {
                    drawArc(
                        color = BgMain,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                
                Canvas(modifier = Modifier.size(canvasSize)) {
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
                        text = "RESTANTE",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextGray,
                        letterSpacing = 1.sp
                    )
                    
                    val tiempoTexto = if (horas > 0) {
                        "%02d:%02d:%02d".format(horas, minsRestantes, segundos)
                    } else {
                        "%02d:%02d".format(minsRestantes, segundos)
                    }
                    
                    Text(
                        text = tiempoTexto,
                        fontSize = if (horas > 0) 44.sp else 56.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        lineHeight = 44.sp
                    )
                    
                    Text(
                        text = if (horas > 0) "hr : min : seg" else "minutos : seg",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextGray,
                        modifier = Modifier.padding(top = 2.dp)
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
