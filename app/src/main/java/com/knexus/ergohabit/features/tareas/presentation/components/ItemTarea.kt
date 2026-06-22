package com.knexus.ergohabit.features.tareas.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.knexus.ergohabit.features.tareas.domain.entities.TareaEnfoque
import com.knexus.ergohabit.ui.theme.*

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.ui.unit.sp

@Composable
fun ItemTarea(
    tarea: TareaEnfoque,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isCompletada = tarea.idEstado == 2
    val categoryColor = when(tarea.idCategoria) {
        1 -> MoradoAcento
        2 -> GreenPrimary
        3 -> PurpleAccent
        else -> OrangeAccent
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = BgWhite,
        border = if (isSelected && !isCompletada) androidx.compose.foundation.BorderStroke(1.dp, categoryColor) else null
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicador de color lateral (solo si no está completada o opcional según diseño)
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (isCompletada) TextGray.copy(alpha = 0.3f) else categoryColor)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tarea.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isCompletada) TextGray else TextPrimary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = when(tarea.idCategoria) {
                            1 -> "🎓"
                            2 -> "🧘"
                            3 -> "🧠"
                            else -> "📌"
                        },
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = when(tarea.idCategoria) {
                            1 -> "Académica"
                            2 -> "Bienestar"
                            3 -> "Enfoque Profundo"
                            else -> "General"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                }
            }
            
            if (isCompletada) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = GreenPrimary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = TextGray.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
