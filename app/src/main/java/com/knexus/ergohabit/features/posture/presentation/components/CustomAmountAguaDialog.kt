package com.knexus.ergohabit.features.posture.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
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
import androidx.compose.ui.window.Dialog

@Composable
fun CustomAmountAguaDialog(
    amount: Int,
    vasos: Float,
    onAjustar: (Boolean) -> Unit,
    onConfirmar: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Drag handle visual
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray.copy(alpha = 0.5f))
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Cantidad personalizada",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E392A)
                )
                
                Text(
                    text = "Ingresa cuánta agua tomaste",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Selector central
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { onAjustar(false) },
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F4F3))
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = null, tint = Color.Gray)
                    }

                    Surface(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .width(140.dp)
                            .height(90.dp)
                            .border(1.dp, Color(0xFF67B7BD).copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
                        color = Color(0xFFF0F9F8),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            val (valor, unidad) = if (amount <= 900) amount.toString() to "ml" else String.format(java.util.Locale.US, "%.1f", amount/1000f) to "L"
                            Text(
                                text = valor,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4DB6AC)
                            )
                            Text(
                                text = unidad,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    IconButton(
                        onClick = { onAjustar(true) },
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F4F3))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                // Slider visual simple
                LinearProgressIndicator(
                    progress = { (amount / 2000f).coerceAtMost(1f) },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(6.dp)
                        .clip(CircleShape),
                    color = Color(0xFF4DB6AC),
                    trackColor = Color(0xFFEEEEEE)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "≈ $vasos vasos de 250ml",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Botón Agregar
                Button(
                    onClick = onConfirmar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4DB6AC)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "✓ Agregar ${amount}ml",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Botón Cancelar
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F4F3)),
                    shape = RoundedCornerShape(14.dp),
                    elevation = null
                ) {
                    Text(
                        text = "Cancelar",
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
