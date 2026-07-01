package com.knexus.ergohabit.features.posture.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.knexus.ergohabit.features.posture.presentation.components.BarraNavegacionInferior
import com.knexus.ergohabit.features.posture.presentation.viewmodel.NutricionViewModel
import com.knexus.ergohabit.ui.theme.*

@Composable
fun NutricionScreen(
    navController: NavHostController,
    viewModel: NutricionViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToConfigNutricion: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error, state.successMessage) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        containerColor = BgMain,
        bottomBar = { BarraNavegacionInferior(navController) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { onNavigateBack() }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBackIosNew,
                        contentDescription = "Regresar",
                        tint = TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "MICRO-HÁBITO",
                        fontSize = 10.sp,
                        color = TextSecondary,
                        letterSpacing = 0.06.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Nutrición",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {

                Spacer(modifier = Modifier.height(8.dp))

                // ── TÍTULO ────────────────────────────────────
                Text(
                    text = "Nutrición",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = NutricionVerde,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Come a tiempo para rendir mejor",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 20.dp),
                    textAlign = TextAlign.Center
                )

                // ── CARD PRINCIPAL VERDE ──────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(NutricionVerdeBg)
                        .padding(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "🍎", fontSize = 52.sp)

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "${state.comidasCompletadas} / ${state.comidasObjetivo} comidas",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "Hoy",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        LinearProgressIndicator(
                            progress = { state.porcentaje },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.3f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = state.mensajeFaltante.ifBlank {
                                when (state.comidasRestantes) {
                                    0 -> "¡Meta cumplida de hoy!"
                                    1 -> "Te falta 1 comida"
                                    else -> "Te faltan ${state.comidasRestantes} comidas"
                                }
                            },
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── HORARIOS DE COMIDA ────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(NutricionCardBg)
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = "Horarios de Comida",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Configura tus horarios ideales",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
                        )

                        FilaComida(
                            emoji = "🥐",
                            nombre = "Desayuno",
                            completada = state.desayunoCompletado,
                            onClick = { viewModel.marcarComida("DESAYUNO", !state.desayunoCompletado) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        FilaComida(
                            emoji = "🍽️",
                            nombre = "Comida",
                            completada = state.comidaCompletada,
                            onClick = { viewModel.marcarComida("COMIDA", !state.comidaCompletada) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        FilaComida(
                            emoji = "🌙",
                            nombre = "Cena",
                            completada = state.cenaCompletada,
                            onClick = { viewModel.marcarComida("CENA", !state.cenaCompletada) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ── CONFIGURAR HORARIOS ───────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFE8F5E9)) // Verde muy clarito como la imagen
                        .clickable { onNavigateToConfigNutricion() }
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Icono en círculo
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFD0E7D2)) // Fondo del círculo
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = null,
                                tint = Color(0xFF458C5E), // Verde del icono
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Configurar horarios",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0D47A1) // Azul oscuro como el título en la imagen
                            )
                            Text(
                                text = "Ajusta tus horarios de comida",
                                fontSize = 12.sp,
                                color = Color(0xFF90A4AE) // Gris azulado para el subtítulo
                            )
                        }
                        
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                            contentDescription = null,
                            tint = Color(0xFF458C5E),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ── TIPS DE NUTRICIÓN ─────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(NutricionTipsBg)
                        .padding(20.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🍎", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Tips de Nutrición",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        NutricionTipBullet("🥐 Desayuna antes de las 9 AM para activar tu metabolismo")
                        Spacer(modifier = Modifier.height(10.dp))
                        NutricionTipBullet("🍽️ Come entre 13:00-15:00 para mejor concentración en la tarde")
                        Spacer(modifier = Modifier.height(10.dp))
                        NutricionTipBullet("🌙 Cena ligera 2-3 horas antes de dormir")
                        Spacer(modifier = Modifier.height(10.dp))
                        NutricionTipBullet("💚 Come a la misma hora todos los días para mejor rendimiento")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ── COMPONENTE FILA COMIDA ────────────────────────────────
@Composable
fun FilaComida(
    emoji: String,
    nombre: String,
    completada: Boolean,
    onClick: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(NutricionGrisBg)
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Text(text = emoji, fontSize = 22.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = nombre,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        if (completada) NutricionVerde.copy(alpha = 0.3f)
                        else Color(0xFFE0E0E0)
                    )
            )
        }
        if (completada) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = NutricionVerde,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// ── COMPONENTE TIP BULLET ─────────────────────────────────
@Composable
fun NutricionTipBullet(texto: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text(
            text = "●",
            fontSize = 10.sp,
            color = NutricionVerde,
            modifier = Modifier.padding(top = 3.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = texto,
            fontSize = 13.sp,
            color = TextSecondary,
            lineHeight = 18.sp
        )
    }
}
