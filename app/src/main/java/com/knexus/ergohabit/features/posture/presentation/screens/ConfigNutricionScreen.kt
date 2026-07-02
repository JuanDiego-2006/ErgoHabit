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
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigNutricionScreen(
    viewModel: ConfigNutricionViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onComenzar: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error, state.successMessage) {
        state.error?.let { snackbarHostState.showSnackbar(it); viewModel.clearMessages() }
        state.successMessage?.let { snackbarHostState.showSnackbar(it); viewModel.clearMessages() }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF1F8F1)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // ── HEADER ────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White).clickable { onNavigateBack() }
                ) {
                    Icon(Icons.Outlined.ArrowBackIosNew, contentDescription = null, tint = Color(0xFF458C5E), modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("MICRO-HÁBITO", fontSize = 11.sp, color = Color(0xFF90A4AE), fontWeight = FontWeight.Bold)
                    Text("Nutrición", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E4D3B))
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                Text(text = "🍴", fontSize = 80.sp)
                Spacer(modifier = Modifier.height(24.dp))
                Text("Configura tu Nutrición", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color(0xFF4DB686), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Toca cada comida para establecer su horario.\nTe avisaremos 10 minutos antes.", fontSize = 14.sp, color = Color(0xFF78909C), textAlign = TextAlign.Center, lineHeight = 20.sp)
                Spacer(modifier = Modifier.height(40.dp))

                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("PERSONALIZA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4DB686), letterSpacing = 1.sp)
                        Text("Horarios de comidas", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF263238))
                        Text("Toca para editar cada horario", fontSize = 13.sp, color = Color(0xFF90A4AE), modifier = Modifier.padding(top = 4.dp, bottom = 20.dp))

                        ItemHorarioConfig("🥐", "Desayuno", state.horaDesayuno) { viewModel.abrirEditor("Desayuno", "🥐", state.horaDesayuno) }
                        Spacer(modifier = Modifier.height(12.dp))
                        ItemHorarioConfig("🍽️", "Comida", state.horaComida) { viewModel.abrirEditor("Comida", "🍽️", state.horaComida) }
                        Spacer(modifier = Modifier.height(12.dp))
                        ItemHorarioConfig("🌙", "Cena", state.horaCena) { viewModel.abrirEditor("Cena", "🌙", state.horaCena) }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
                Button(
                    onClick = { onComenzar() },
                    modifier = Modifier.fillMaxWidth().height(65.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF458C5E))
                ) {
                    Text("Comenzar a Usar", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (state.showSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.cerrarEditor() },
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            SelectorHoraSheetContent(
                state.comidaEditando, state.emojiEditando, state.horaTemp, state.isLoading, state.error,
                { viewModel.onHoraTempChange(it) }, { viewModel.confirmarHora() }, { viewModel.cerrarEditor() }
            )
        }
    }
}

@Composable
fun SelectorHoraSheetContent(
    comida: String, emoji: String, horaActual: String, isLoading: Boolean, error: String?,
    onHoraChanged: (String) -> Unit, onConfirmar: () -> Unit, onCerrar: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFF1F8F1)), contentAlignment = Alignment.Center) { Text(emoji, fontSize = 24.sp) }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text("HORARIO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4DB686))
                Text(comida, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E4D3B))
            }
            IconButton(onClick = onCerrar, modifier = Modifier.clip(CircleShape).background(Color(0xFFF5F5F5)).size(32.dp)) {
                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
            }
        }
        Spacer(Modifier.height(32.dp))
        Text("SELECCIONA LA HORA", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF90A4AE))
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(24.dp)).background(Color(0xFFF1F8F1)).border(1.dp, Color(0xFF4DB686).copy(alpha = 0.2f), RoundedCornerShape(24.dp)).clickable { mostrarTimePicker(context, horaActual, onHoraChanged) },
            contentAlignment = Alignment.Center
        ) {
            Text(formatearAMPM(horaActual), fontSize = 48.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E4D3B))
        }
        Spacer(Modifier.height(24.dp))
        val sugerencias = when(comida) {
            "Desayuno" -> listOf("07:00", "07:30", "08:00", "08:30")
            "Comida" -> listOf("14:00", "14:30", "15:00", "15:30")
            "Cena" -> listOf("20:00", "20:15", "20:30", "20:45")
            else -> emptyList()
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            sugerencias.forEach { s ->
                SuggestionChip(
                    onClick = { onHoraChanged(s) },
                    label = { Text(formatearAMPM(s)) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if (normalizarParaComparar(s) == normalizarParaComparar(horaActual)) Color(0xFF1E4D3B) else Color(0xFFF5F5F5),
                        labelColor = if (normalizarParaComparar(s) == normalizarParaComparar(horaActual)) Color.White else Color(0xFF78909C)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
            }
        }
        if (error != null) {
            Text(error, color = Color(0xFFC62828), fontSize = 13.sp, modifier = Modifier.padding(vertical = 16.dp), textAlign = TextAlign.Center)
        } else {
            Spacer(Modifier.height(32.dp))
        }
        Button(
            onClick = onConfirmar, enabled = !isLoading,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4DB686))
        ) {
            if (isLoading) CircularProgressIndicator(Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
            else { Icon(Icons.Outlined.Check, null); Spacer(Modifier.width(8.dp)); Text("Guardar horario", fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
fun ItemHorarioConfig(emoji: String, titulo: String, hora: String, onClick: () -> Unit) {
    val display = if (hora == "00:00" || hora.isBlank()) "12:00 AM" else formatearAMPM(hora)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(20.dp)).clickable { onClick() }.padding(16.dp)
    ) {
        Box(Modifier.size(44.dp).clip(CircleShape).background(Color(0xFFF5F5F5)), contentAlignment = Alignment.Center) { Text(emoji, fontSize = 24.sp) }
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(titulo, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF263238))
            Text(display, fontSize = 13.sp, color = Color(0xFF4DB686), fontWeight = FontWeight.Medium)
        }
        Icon(Icons.AutoMirrored.Outlined.ArrowForwardIos, null, tint = Color(0xFFE0E0E0), modifier = Modifier.size(14.dp))
    }
}

private fun mostrarTimePicker(context: android.content.Context, horaActual: String, onTimeSelected: (String) -> Unit) {
    val cal = Calendar.getInstance()
    try {
        val partes = normalizarParaComparar(horaActual).split(":")
        cal.set(Calendar.HOUR_OF_DAY, partes[0].toInt())
        cal.set(Calendar.MINUTE, partes[1].toInt())
    } catch (e: Exception) {}
    TimePickerDialog(context, { _, h, m -> onTimeSelected(String.format(Locale.ROOT, "%02d:%02d", h, m)) }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
}

private fun normalizarParaComparar(h: String): String {
    if (h.isBlank() || h == "00:00") return "08:00"
    return try {
        val up = h.trim().uppercase()
        if (up.contains("AM") || up.contains("PM")) {
            val p = up.split(" ")
            val hp = p[0].split(":")
            var hr = hp[0].toInt()
            if (up.contains("PM") && hr < 12) hr += 12
            if (up.contains("AM") && hr == 12) hr = 0
            String.format(Locale.ROOT, "%02d:%s", hr, hp[1].take(2))
        } else h.take(5)
    } catch (e: Exception) { "08:00" }
}

private fun formatearAMPM(h24: String): String {
    if (h24.isBlank() || h24 == "00:00") return "12:00 AM"
    return try {
        val p = h24.split(":")
        var hr = p[0].toInt()
        val m = p[1].take(2).toInt()
        val sfx = if (hr >= 12) "PM" else "AM"
        if (hr > 12) hr -= 12
        if (hr == 0) hr = 12
        String.format(Locale.getDefault(), "%d:%02d %s", hr, m, sfx)
    } catch (e: Exception) { h24 }
}
