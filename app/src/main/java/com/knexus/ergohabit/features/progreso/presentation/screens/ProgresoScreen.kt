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
                onHabitoClick = { viewModel.seleccionarHabito(it.id) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Detalle del Hábito Seleccionado (Ej: Sueño, Agua, Postura)
            uiState.detalleHabito?.let { detalle ->
                DetalleHabitoCard(detalle)
                Spacer(modifier = Modifier.height(24.dp))
            }

            Text(
                text = "CUMPLIMIENTO DE HÁBITOS · 21 DÍAS",
                style = MaterialTheme.typography.labelSmall,
                color = TextGray,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tarjeta de Tendencia General (Cumplimiento de todos los hábitos)
            TendenciaGeneralCard(
                porcentaje = uiState.porcentajeTendencia,
                tendencia = uiState.tendencia
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun HabitosGrid(habitos: List<HabitoProgreso>, onHabitoClick: (HabitoProgreso) -> Unit) {
    val displayHabitos = if (habitos.isEmpty()) {
        listOf(
            HabitoProgreso(1, "Sueño", "🌙", "#7C6FF7", 75),
            HabitoProgreso(2, "Agua", "💧", "#29B6F6", 60),
            HabitoProgreso(3, "Ejercicio", "🏃", "#34C97A", 40),
            HabitoProgreso(4, "Postura", "🧘", "#2E7D52", 90)
        )
    } else habitos

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            HabitoCard(displayHabitos[0], Modifier.weight(1f)) { onHabitoClick(displayHabitos[0]) }
            HabitoCard(displayHabitos[1], Modifier.weight(1f)) { onHabitoClick(displayHabitos[1]) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            HabitoCard(displayHabitos[2], Modifier.weight(1f)) { onHabitoClick(displayHabitos[2]) }
            HabitoCard(displayHabitos[3], Modifier.weight(1f)) { onHabitoClick(displayHabitos[3]) }
        }
    }
}

@Composable
fun HabitoCard(habito: HabitoProgreso, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val baseColor = try {
        Color(android.graphics.Color.parseColor(habito.colorHex))
    } catch (e: Exception) {
        GreenPrimary
    }
    
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = baseColor.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(24.dp),
        modifier = modifier.height(140.dp)
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
    // Título dinámico
    val tituloGrafica = when(detalle.idHabito) {
        2 -> "HIDRATACIÓN (L) · ÚLTIMOS 7 DÍAS"
        4 -> "ALERTAS DE POSTURA · ÚLTIMOS 7 DÍAS"
        else -> "${detalle.titulo.uppercase()} · ÚLTIMOS 7 DÍAS"
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
            // En postura todas las barras son rojas según la imagen
            val barColor = if (idHabito == 4) Color(0xFFE54D4D) else {
                if (registro.esMetaCumplida) Color(0xFF5CB38C) else Color(0xFFE54D4D)
            }
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

@Composable
fun TendenciaGeneralCard(porcentaje: String, tendencia: List<ProgresoDia>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tendencia general",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = porcentaje,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            BarChart(tendencia)

            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Días del experimento (día 1 – 21)",
                style = MaterialTheme.typography.labelSmall,
                color = TextGray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun BarChart(data: List<ProgresoDia>) {
    val displayData = if (data.isEmpty()) {
        List(21) { i -> 
            ProgresoDia(i + 1, (0.3f + (Math.random() * 0.7f)).toFloat())
        }
    } else data

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            displayData.forEach { dia ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(dia.valor)
                        .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                        .background(GreenPrimary)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("1", style = MaterialTheme.typography.labelSmall, color = TextGray)
            Text("6", style = MaterialTheme.typography.labelSmall, color = TextGray)
            Text("11", style = MaterialTheme.typography.labelSmall, color = TextGray)
            Text("16", style = MaterialTheme.typography.labelSmall, color = TextGray)
            Text("21", style = MaterialTheme.typography.labelSmall, color = TextGray)
        }
    }
}
