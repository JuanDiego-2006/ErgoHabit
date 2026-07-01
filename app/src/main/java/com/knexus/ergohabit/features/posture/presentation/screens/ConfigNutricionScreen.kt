package com.knexus.ergohabit.features.posture.presentation.screens

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.knexus.ergohabit.features.posture.presentation.viewmodel.ConfigNutricionViewModel
import com.knexus.ergohabit.ui.theme.*
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigNutricionScreen(
    viewModel: ConfigNutricionViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onComenzar: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F8F1)) // Fondo verde muy claro como la imagen
            .verticalScroll(rememberScrollState())
    ) {
        // ── HEADER ────────────────────────────────────────
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { onNavigateBack() }
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBackIosNew,
                    contentDescription = "Regresar",
                    tint = Color(0xFF458C5E),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "MICRO-HÁBITO",
                    fontSize = 11.sp,
                    color = Color(0xFF90A4AE),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Nutrición",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E4D3B)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(text = "🍴", fontSize = 80.sp)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Configura tu Nutrición",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF4DB686), // Verde de la imagen
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Toca cada comida para establecer su horario.\nTe avisaremos 30 minutos antes.",
                fontSize = 14.sp,
                color = Color(0xFF78909C),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // ── CARD HORARIOS ─────────────────────────────
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "PERSONALIZA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4DB686),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Horarios de comidas",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF263238)
                    )
                    Text(
                        text = "Toca para editar cada horario",
                        fontSize = 13.sp,
                        color = Color(0xFF90A4AE),
                        modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                    )

                    // Desayuno
                    ItemHorarioConfig(
                        emoji = "🥐",
                        titulo = "Desayuno",
                        hora = state.horaDesayuno,
                        onClick = { viewModel.abrirEditor("Desayuno", "🥐", state.horaDesayuno) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Comida
                    ItemHorarioConfig(
                        emoji = "🍽️",
                        titulo = "Comida",
                        hora = state.horaComida,
                        onClick = { viewModel.abrirEditor("Comida", "🍽️", state.horaComida) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Cena
                    ItemHorarioConfig(
                        emoji = "🌙",
                        titulo = "Cena",
                        hora = state.horaCena,
                        onClick = { viewModel.abrirEditor("Cena", "🌙", state.horaCena) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // ── BOTÓN COMENZAR ────────────────────────────
            Button(
                onClick = {
                    viewModel.guardar()
                    onComenzar()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF458C5E))
            ) {
                Text(
                    text = "Comenzar a Usar",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // ── BOTTOM SHEET PARA SELECCIONAR HORA ────────────────
    if (state.showSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.cerrarEditor() },
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFFE0E0E0)) }
        ) {
            SelectorHoraSheetContent(
                comida = state.comidaEditando,
                emoji = state.emojiEditando,
                horaActual = state.horaTemp,
                onHoraChanged = { viewModel.onHoraTempChange(it) },
                onConfirmar = { viewModel.confirmarHora() },
                onCerrar = { viewModel.cerrarEditor() }
            )
        }
    }
}

@Composable
fun SelectorHoraSheetContent(
    comida: String,
    emoji: String,
    horaActual: String,
    onHoraChanged: (String) -> Unit,
    onConfirmar: () -> Unit,
    onCerrar: () -> Unit
) {
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header del Sheet
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFF1F8F1))
            ) {
                Text(text = emoji, fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "HORARIO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4DB686))
                Text(text = comida, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E4D3B))
            }
            IconButton(
                onClick = onCerrar,
                modifier = Modifier.clip(CircleShape).background(Color(0xFFF5F5F5)).size(32.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "SELECCIONA LA HORA",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF90A4AE),
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Caja de visualización de hora
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFF1F8F1))
                .border(1.dp, Color(0xFF4DB686).copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                .clickable {
                    mostrarTimePicker(context, horaActual) { onHoraChanged(it) }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = formatearAMPM(horaActual),
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1E4D3B)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Horarios sugeridos
        Text(
            text = "HORARIOS SUGERIDOS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF90A4AE),
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(12.dp))

        val sugerencias = when(comida) {
            "Desayuno" -> listOf("07:00", "07:30", "08:00", "08:30")
            "Comida" -> listOf("13:00", "13:30", "14:00", "14:30")
            "Cena" -> listOf("19:00", "19:30", "20:00", "20:30")
            else -> emptyList()
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            sugerencias.forEach { sugerencia ->
                val isSelected = sugerencia == horaActual
                SuggestionChip(
                    onClick = { onHoraChanged(sugerencia) },
                    label = { Text(formatearAMPM(sugerencia)) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if (isSelected) Color(0xFF1E4D3B) else Color(0xFFF5F5F5),
                        labelColor = if (isSelected) Color.White else Color(0xFF78909C)
                    ),
                    border = null,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón Guardar
        Button(
            onClick = onConfirmar,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4DB686))
        ) {
            Icon(Icons.Outlined.Check, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Guardar horario", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ItemHorarioConfig(
    emoji: String,
    titulo: String,
    hora: String,
    onClick: () -> Unit
) {
    val displayHora = if (hora == "00:00" || hora.isBlank()) "Sin establecer" else formatearAMPM(hora)
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFFF5F5F5))
        ) {
            Text(text = emoji, fontSize = 24.sp)
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = titulo,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF263238)
            )
            Text(
                text = displayHora,
                fontSize = 13.sp,
                color = Color(0xFF4DB686),
                fontWeight = FontWeight.Medium
            )
        }
        
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
            contentDescription = null,
            tint = Color(0xFFE0E0E0),
            modifier = Modifier.size(14.dp)
        )
    }
}

private fun mostrarTimePicker(context: android.content.Context, horaActual: String, onTimeSelected: (String) -> Unit) {
    val cal = Calendar.getInstance()
    if (horaActual.isNotBlank() && horaActual != "00:00") {
        try {
            val partes = horaActual.split(":")
            cal.set(Calendar.HOUR_OF_DAY, partes[0].toInt())
            cal.set(Calendar.MINUTE, partes[1].toInt())
        } catch (e: Exception) {}
    }
    
    TimePickerDialog(
        context,
        { _, hour, minute ->
            onTimeSelected(String.format("%02d:%02d", hour, minute))
        },
        cal.get(Calendar.HOUR_OF_DAY),
        cal.get(Calendar.MINUTE),
        false
    ).show()
}

private fun formatearAMPM(hora24: String): String {
    return try {
        val partes = hora24.split(":")
        var h = partes[0].toInt()
        val m = partes[1].toInt()
        val suffix = if (h >= 12) "PM" else "AM"
        if (h > 12) h -= 12
        if (h == 0) h = 12
        String.format("%d:%02d %s", h, m, suffix)
    } catch (e: Exception) {
        hora24
    }
}
