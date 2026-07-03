package com.knexus.ergohabit.features.posture.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.util.Locale

@Composable
fun PesoEstaturaDialog(
    pesoInput: String,
    estaturaInput: String,
    editandoPeso: Boolean,
    metaRecomendada: Int,
    onCambiarModo: (Boolean) -> Unit,
    onPesoChange: (String) -> Unit,
    onEstaturaChange: (String) -> Unit,
    onGuardar: () -> Unit,
    onAtras: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Peso y estatura",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1E392A)
                )
                Text(
                    text = "Toca el valor que quieres ajustar",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Selector de modo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SelectableDataCard(
                        label = "PESO",
                        value = pesoInput,
                        unit = "kg",
                        isSelected = editandoPeso,
                        modifier = Modifier.weight(1f),
                        onClick = { onCambiarModo(true) }
                    )
                    SelectableDataCard(
                        label = "ESTATURA",
                        value = estaturaInput,
                        unit = "cm",
                        isSelected = !editandoPeso,
                        modifier = Modifier.weight(1f),
                        onClick = { onCambiarModo(false) }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Entrada de texto central
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = if (editandoPeso) pesoInput else estaturaInput,
                        onValueChange = { if (editandoPeso) onPesoChange(it) else onEstaturaChange(it) },
                        textStyle = TextStyle(
                            fontSize = 64.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E392A),
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.width(180.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color(0xFF67B7BD),
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                    
                    Text(
                        text = if (editandoPeso) "kg" else "cm",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E392A),
                        modifier = Modifier.padding(top = 30.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Meta recomendada
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFE9F5F2),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 18.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("📊", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Meta recomendada",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E392A),
                                maxLines = 1
                            )
                        }
                        
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.End
                        ) {
                            val litros = metaRecomendada / 1000f
                            val (valor, unidad) = if (metaRecomendada <= 900) metaRecomendada.toString() to "ml/día" else String.format(java.util.Locale.US, "%.1f", litros) to "L/día"
                            
                            Text(
                                text = valor,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF67B7BD),
                                lineHeight = 24.sp
                            )
                            Text(
                                text = unidad,
                                fontSize = 11.sp,
                                color = Color(0xFF5E7D75),
                                modifier = Modifier.padding(bottom = 4.dp, start = 2.dp),
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onAtras,
                        modifier = Modifier.weight(1f).height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F4F3)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Atrás", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onGuardar,
                        modifier = Modifier.weight(1.5f).height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF67B7BD)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Guardar", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SelectableDataCard(
    label: String,
    value: String,
    unit: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) Color(0xFF67B7BD) else Color(0xFFF1F4F3)
    val bgColor = if (isSelected) Color.White else Color(0xFFF1F4F3).copy(alpha = 0.5f)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(2.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(if(label == "PESO") "⚖️" else "📏", fontSize = 10.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color(0xFF67B7BD) else Color.Gray
            )
        }
        Text(
            text = value,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = if (isSelected) Color(0xFF67B7BD) else Color(0xFF1E392A),
            maxLines = 1
        )
        Text(
            text = unit,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}
