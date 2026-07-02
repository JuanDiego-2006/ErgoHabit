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
import androidx.compose.ui.draw.rotate
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
    // Título dinámico ajustado al estilo de la imagen
    val tituloPrincipal = when (detalle.idHabito) {
        1 -> "Horas de sueño"
        2 -> "Hidratación (ml)"
        3 -> "Distancia recorrida (km)"
        4 -> "Alertas de postura"
        else -> {
            detalle.titulo
                .replace(" - Semana Actual", "", ignoreCase = true)
                .replace(" Semana Actual", "", ignoreCase = true)
                .replace("Semana Actual", "", ignoreCase = true)
                .trim()
                .ifBlank { "Detalle del hábito" }
        }
    }
    val subtitulo = "ÚLTIMOS 7 DÍAS"

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
                text = tituloPrincipal,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF1E392A),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitulo,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFB0B0B0),
                fontWeight = FontWeight.Normal,
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
                        val posLabel = if (detalle.idHabito == 2) "Meta cumplida" else detalle.leyendaPositiva
                        val negLabel = if (detalle.idHabito == 2) "Bajo la meta" else detalle.leyendaNegativa

                        CircleDot(Color(0xFF5CB38C))
                        Text(
                            text = posLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF5E9C76),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 6.dp, end = 16.dp)
                        )
                        CircleDot(Color(0xFFE54D4D))
                        Text(
                            text = negLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF5E9C76),
                            fontWeight = FontWeight.Medium,
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
    val unidad = when(idHabito) {
        2 -> "L"
        3 -> "km"
        4 -> "⚠️"
        else -> "h"
    }

    val maxData = registros.maxOfOrNull { it.valor } ?: 0f
    
    // Forzamos el tope y pasos de forma estricta
    val (baseTop, step) = when (idHabito) {
        2 -> 5f to 1f  // Agua: 5, 4, 3, 2, 1, 0
        3 -> 20f to 5f // Ejercicio: 20, 15, 10, 5, 0
        4 -> 500f to 100f // Postura: 500, 400, 300, 200, 100, 0
        else -> 10f to 2f // Sueño: 10, 8, 6, 4, 2, 0
    }
    
    // Si los datos o la meta superan el tope base, ajustamos proporcionalmente
    val finalTop = maxOf(baseTop, maxData, meta).let {
        if (it > baseTop) (Math.ceil(it / step.toDouble()).toInt() * step.toInt()).toFloat()
        else baseTop
    }
    
    val yLabels = mutableListOf<String>()
    var curr = finalTop
    while (curr >= -0.01f) {
        yLabels.add(if (idHabito == 2 && curr > 0.1f) "%.1f".format(curr) else curr.toInt().toString())
        curr -= step
    }

    val horizontalPadding = 8.dp
    val yAxisWidth = 32.dp
    val axisGap = 8.dp
    val totalStartPadding = yAxisWidth + axisGap
    val labelReservedHeight = 24.dp // Espacio para que las etiquetas no se corten arriba

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        Row(modifier = Modifier.weight(1f)) {
            // Eje Y: Texto Lateral
            Box(
                modifier = Modifier.fillMaxHeight().padding(bottom = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when(idHabito) {
                        2 -> "Litros"
                        3 -> "km"
                        4 -> "Alertas"
                        else -> "Horas"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFB0B0B0),
                    modifier = Modifier.rotate(-90f)
                )
            }

            Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Box(modifier = Modifier.weight(1f)) {
                    // Cuadrícula y Etiquetas Y
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = labelReservedHeight),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        yLabels.forEach { label ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.height(16.dp).padding(end = horizontalPadding)
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFB0B0B0),
                                    modifier = Modifier.width(yAxisWidth),
                                    textAlign = TextAlign.End
                                )
                                Spacer(modifier = Modifier.width(axisGap))
                                HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp, modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    // BARRAS Y ETIQUETAS FLOTANTES
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = totalStartPadding, end = horizontalPadding)
                            .padding(top = labelReservedHeight + 8.dp) // +8dp para alinear con el centro de la línea superior
                            .padding(bottom = 8.dp), // Alinea con línea 0 (16dp/2)
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        registros.forEach { registro ->
                            val isHoy = registro.etiqueta == "Hoy"
                            val esFuturo = !isHoy && (registros.indexOf(registro) > registros.indexOf(registros.find { it.etiqueta == "Hoy" } ?: registros.last()))

                            val barColor = when {
                                idHabito == 4 -> Color(0xFFE54D4D)
                                esFuturo -> Color.LightGray.copy(alpha = 0.3f)
                                registro.esMetaCumplida && registro.valor > 0 -> Color(0xFF5CB38C)
                                else -> Color(0xFFE54D4D)
                            }

                            val barHeightFraction = (registro.valor / finalTop).coerceIn(0f, 1f)

                            Box(
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                // Barra con altura matemática exacta (Rectangular)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(if (idHabito == 4) 0.7f else 0.55f)
                                        .fillMaxHeight(barHeightFraction)
                                        .background(barColor)
                                )
                                
                                // Etiqueta superior flotante
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxHeight((1f - barHeightFraction).coerceAtLeast(0f))
                                            .fillMaxWidth(),
                                        verticalArrangement = Arrangement.Bottom,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        val valorFormateado = when (idHabito) {
                                            2 -> "%.1f".format(registro.valor)
                                            3 -> "%.2f".format(registro.valor)
                                            else -> registro.valor.toInt().toString()
                                        }
                                        Text(
                                            text = "$valorFormateado$unidad",
                                            color = barColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = if (idHabito == 3) 9.sp else 10.sp,
                                            maxLines = 1,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Eje X: Días centrados con las barras
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = totalStartPadding, end = horizontalPadding),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    registros.forEach { registro ->
                        val isHoy = registro.etiqueta == "Hoy"
                        Text(
                            text = registro.etiqueta,
                            color = if (isHoy) Color(0xFF1E392A) else Color(0xFFB0B0B0),
                            fontWeight = if (isHoy) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
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
