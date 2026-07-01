package com.knexus.ergohabit.features.progreso.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.foundation.Canvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.knexus.ergohabit.features.posture.presentation.components.BarraNavegacionInferior
import com.knexus.ergohabit.features.progreso.domain.entities.DetalleHabito
import com.knexus.ergohabit.features.progreso.domain.entities.HabitoProgreso
import com.knexus.ergohabit.features.progreso.domain.entities.ProgresoDia
import com.knexus.ergohabit.features.progreso.domain.entities.RegistroHabito
import com.knexus.ergohabit.features.progreso.presentation.viewmodel.ProgresoViewModel
import com.knexus.ergohabit.ui.theme.*

@Composable
fun ProgresoScreen(
    navController: NavHostController,
    viewModel: ProgresoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = BgMain,
        bottomBar = { BarraNavegacionInferior(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Progreso",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "PROGRESO DE LA SEMANA · TOCA PARA VER GRÁFICA",
                style = MaterialTheme.typography.labelSmall,
                color = TextGray,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Grid de Hábitos (Progreso de la semana)
            HabitosGrid(
                habitos = uiState.habitos,
                idHabitoSeleccionado = uiState.idHabitoSeleccionado,
                onHabitoClick = { viewModel.seleccionarHabito(it.id) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Detalle del Hábito Seleccionado (Ej: Sueño, Agua, Postura)
            uiState.detalleHabito?.let { detalle ->
                DetalleHabitoCard(detalle)
                Spacer(modifier = Modifier.height(24.dp))
            }

            Text(
                text = "FRASE DEL DÍA",
                style = MaterialTheme.typography.labelSmall,
                color = TextGray,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Nueva Tarjeta de Frase del Día
            FraseDelDiaCard(uiState.frase)
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun FraseDelDiaCard(frase: com.knexus.ergohabit.features.progreso.domain.entities.Frase?) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFDFF1E1)), // Verde clarito como la imagen
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "INSPIRACIÓN DEL DÍA",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF5E9C76),
                    fontWeight = FontWeight.Bold
                )
                Text(text = "\"", color = Color(0xFF5E9C76), fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = frase?.texto ?: "La constancia es la virtud por la que todas las otras virtudes dan fruto.",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E392A),
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = Color(0xFF5E9C76).copy(alpha = 0.2f))

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "ErgoHabit Team", // O podrías usar la categoría si el autor no viene
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF5E9C76),
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
fun HabitosGrid(habitos: List<HabitoProgreso>, idHabitoSeleccionado: Int?, onHabitoClick: (HabitoProgreso) -> Unit) {
    // Definimos los 4 hábitos base con sus iconos y colores protegidos
    val baseHabitos = listOf(
        HabitoProgreso(1, "Sueño", "🌙", "#7C6FF7", 0),
        HabitoProgreso(2, "Agua", "💧", "#29B6F6", 0),
        HabitoProgreso(3, "Ejercicio", "🏃", "#34C97A", 0),
        HabitoProgreso(4, "Postura", "🧘", "#2E7D52", 0)
    )

    // Combinamos con los datos reales pero protegemos Nombre, Icono y Color
    val displayHabitos = baseHabitos.map { base ->
        val real = habitos.find { it.id == base.id }
        if (real != null) {
            // Si existe el dato real, lo usamos pero nos aseguramos que los campos no sean basura
            real.copy(
                nombre = if (real.nombre.isBlank()) base.nombre else real.nombre,
                icono = if (real.icono.isBlank()) base.icono else real.icono,
                colorHex = if (real.colorHex.isBlank() || !real.colorHex.startsWith("#")) base.colorHex else real.colorHex
            )
        } else {
            base
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            HabitoCard(displayHabitos[0], idHabitoSeleccionado == displayHabitos[0].id, Modifier.weight(1f)) { onHabitoClick(displayHabitos[0]) }
            HabitoCard(displayHabitos[1], idHabitoSeleccionado == displayHabitos[1].id, Modifier.weight(1f)) { onHabitoClick(displayHabitos[1]) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            HabitoCard(displayHabitos[2], idHabitoSeleccionado == displayHabitos[2].id, Modifier.weight(1f)) { onHabitoClick(displayHabitos[2]) }
            HabitoCard(displayHabitos[3], idHabitoSeleccionado == displayHabitos[3].id, Modifier.weight(1f)) { onHabitoClick(displayHabitos[3]) }
        }
    }
}

@Composable
fun HabitoCard(habito: HabitoProgreso, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val baseColor = try {
        Color(android.graphics.Color.parseColor(habito.colorHex))
    } catch (e: Exception) {
        GreenPrimary
    }
    
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) baseColor.copy(alpha = 0.2f) else baseColor.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(24.dp),
        modifier = modifier.height(140.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, baseColor) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(2.dp, baseColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(habito.icono, fontSize = 28.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = habito.nombre,
                color = baseColor,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun DetalleHabitoCard(detalle: DetalleHabito) {
    // Título dinámico: Especial para postura o el que venga de la API para los demás
    val tituloGrafica = if (detalle.idHabito == 4) {
        "ALERTAS DE POSTURA · ÚLTIMOS 7 DÍAS"
    } else {
        detalle.titulo.ifBlank { "DETALLE DEL HÁBITO" }.uppercase()
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = tituloGrafica,
                style = MaterialTheme.typography.titleSmall,
                color = Color(0xFF5E9C76),
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            BarChartSieteDias(detalle.registros, detalle.metaValor, detalle.idHabito)

            Spacer(modifier = Modifier.height(24.dp))

            // Línea de Meta (solo si no es postura) y Leyenda
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                if (detalle.idHabito != 4) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
                            drawLine(
                                color = Color.LightGray.copy(alpha = 0.5f),
                                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                                end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            )
                        }
                        Text(
                            text = "META",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF5E9C76),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.background(Color.White).padding(horizontal = 8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (detalle.idHabito == 4) {
                        // Leyenda específica para postura según la imagen
                        Text(
                            text = detalle.leyendaNegativa,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFB0B0B0),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        CircleDot(Color(0xFF5CB38C))
                        Text(
                            text = detalle.leyendaPositiva,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFB0B0B0),
                            modifier = Modifier.padding(start = 6.dp, end = 16.dp)
                        )
                        CircleDot(Color(0xFFE54D4D))
                        Text(
                            text = detalle.leyendaNegativa,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFB0B0B0),
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BarChartSieteDias(registros: List<RegistroHabito>, meta: Float, idHabito: Int) {
    // Determinar unidad según el hábito
    val unidad = when(idHabito) {
        2 -> "L"
        3 -> "km"
        4 -> "⚠️"
        else -> "h"
    }

    // Calculamos el máximo para la escala visual
    val maxData = registros.maxOfOrNull { it.valor } ?: 0f
    val maxVisual = maxOf(if (idHabito == 4) 10f else meta, maxData) * 1.2f
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        registros.forEach { registro ->
            val isHoy = registro.etiqueta == "Hoy"
            
            // --- CORRECCIÓN DE LÓGICA DE COLOR (Trust API) ---
            val esFuturo = !isHoy && (registros.indexOf(registro) > registros.indexOf(registros.find { it.etiqueta == "Hoy" }))
            
            val barColor = when {
                idHabito == 4 -> Color(0xFFE54D4D) // Postura siempre rojo
                esFuturo -> Color.LightGray.copy(alpha = 0.3f) // Futuro en gris
                registro.esMetaCumplida && registro.valor > 0 -> Color(0xFF5CB38C) // Verde si API dice cumplido
                else -> Color(0xFFE54D4D) // Rojo si API dice no cumplido
            }
            // --------------------------------------------------
            // --------------------------------------

            val barHeightFraction = (registro.valor / maxVisual).coerceIn(0.1f, 0.95f)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                // Valor con unidad (ej: 9⚠️ o 2.1L)
                val valorFormateado = if (idHabito == 4) registro.valor.toInt().toString() else registro.valor.toString()
                Text(
                    text = "$valorFormateado$unidad",
                    color = barColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .width(if (idHabito == 4) 42.dp else 38.dp) // Postura tiene barras un poco más anchas
                            .fillMaxHeight(barHeightFraction)
                            .clip(RoundedCornerShape(topStart = 100.dp, topEnd = 100.dp))
                            .background(barColor)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = registro.etiqueta,
                    color = if (isHoy) Color(0xFF1E392A) else Color(0xFFB0B0B0),
                    fontWeight = if (isHoy) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 13.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun CircleDot(color: Color) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(color)
    )
}
