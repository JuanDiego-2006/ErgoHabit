package com.knexus.ergohabit.features.tareas.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.knexus.ergohabit.ui.theme.*

@Composable
fun SelectorDuracionDialog(
    input: String,
    onNumeroClick: (String) -> Unit,
    onBorrarClick: () -> Unit,
    onConfirmar: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = TextPrimary // Dark Green background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar", color = GreenLight.copy(alpha = 0.7f))
                    }
                    Text("DURACIÓN", color = GreenLight, fontWeight = FontWeight.Bold, fontSize = 16.sp, letterSpacing = 1.sp)
                    TextButton(onClick = onConfirmar) {
                        Text("Listo", color = GreenLight, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Display HH:MM
                val horas = input.substring(0, 2)
                val minutos = input.substring(2, 4)
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(horas, fontSize = 80.sp, fontWeight = FontWeight.Bold, color = GreenLight)
                        Text("h", color = GreenLight.copy(alpha = 0.6f))
                    }
                    Text(":", fontSize = 80.sp, fontWeight = FontWeight.Bold, color = GreenLight.copy(alpha = 0.6f), modifier = Modifier.padding(horizontal = 16.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(minutos, fontSize = 80.sp, fontWeight = FontWeight.Bold, color = GreenLight)
                        Text("min", color = GreenLight.copy(alpha = 0.6f))
                    }
                }

                Spacer(modifier = Modifier.weight(1.3f))

                // Teclado Numérico
                val numeros = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("", "0", "delete")
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    numeros.forEach { fila ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            fila.forEach { item ->
                                when (item) {
                                    "" -> Spacer(modifier = Modifier.size(80.dp))
                                    "delete" -> {
                                        Box(
                                            modifier = Modifier
                                                .size(80.dp)
                                                .clip(CircleShape)
                                                .clickable { onBorrarClick() },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Backspace, 
                                                contentDescription = "Borrar", 
                                                tint = GreenLight,
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                    }
                                    else -> {
                                        Box(
                                            modifier = Modifier
                                                .size(80.dp)
                                                .clip(CircleShape)
                                                .clickable { onNumeroClick(item) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(item, fontSize = 32.sp, fontWeight = FontWeight.Medium, color = GreenLight)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}
