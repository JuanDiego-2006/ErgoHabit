package com.knexus.ergohabit.features.tareas.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knexus.ergohabit.features.tareas.domain.entities.CategoriaTarea
import com.knexus.ergohabit.ui.theme.*

@Composable
fun SheetNuevaTarea(
    titulo: String,
    onTituloChange: (String) -> Unit,
    categorias: List<CategoriaTarea>,
    categoriaId: Int,
    onCategoriaSelect: (Int) -> Unit,
    duracionMinutos: Int,
    onDuracionClick: () -> Unit,
    onAgregarClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        // Header con botón cerrar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Nueva Tarea",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(BgMain)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TextGray, modifier = Modifier.size(18.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        // TÍTULO
        Text(
            "TÍTULO *",
            style = MaterialTheme.typography.labelMedium,
            color = TextGray,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = titulo,
            onValueChange = onTituloChange,
            placeholder = { Text("Ej. Reporte de laboratorio", color = TextGray.copy(alpha = 0.5f)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = BgMain,
                unfocusedContainerColor = BgMain,
                disabledContainerColor = BgMain,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // CATEGORÍA
        Text(
            "CATEGORÍA",
            style = MaterialTheme.typography.labelMedium,
            color = TextGray,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            categorias.forEach { categoria ->
                ItemCategoriaSeleccionable(
                    nombre = categoria.nombre,
                    icono = categoria.icono,
                    isSelected = categoriaId == categoria.id,
                    onClick = { onCategoriaSelect(categoria.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // DURACIÓN
        Text(
            "DURACIÓN",
            style = MaterialTheme.typography.labelMedium,
            color = TextGray,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            onClick = onDuracionClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = BgMain
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(TextPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Timer, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                val horas = duracionMinutos / 60
                val mins = duracionMinutos % 60
                val textoDuracion = when {
                    horas > 0 && mins > 0 -> "$horas h $mins min"
                    horas > 0 -> "$horas h"
                    else -> "$mins min"
                }

                Text(
                    text = textoDuracion,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = TextGray.copy(alpha = 0.5f)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // BOTÓN AGREGAR
        Button(
            onClick = onAgregarClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GreenPrimary.copy(alpha = 0.3f), // Color clarito como la imagen
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(28.dp), // Muy redondeado
            enabled = titulo.isNotBlank()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Agregar Tarea", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}
