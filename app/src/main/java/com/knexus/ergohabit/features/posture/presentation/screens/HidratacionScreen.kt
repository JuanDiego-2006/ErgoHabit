package com.knexus.ergohabit.features.posture.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.knexus.ergohabit.features.posture.presentation.viewmodel.HidratacionViewModel
import com.knexus.ergohabit.ui.theme.*
import com.knexus.ergohabit.features.posture.presentation.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HidratacionScreen(
    viewModel: HidratacionViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
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

    // ── DIÁLOGOS ──────────────────────────────────────
    if (state.mostrarDialogoMeta) {
        AjustarMetaAguaDialog(
            metodoSeleccionado = state.metodoCalculoSeleccionado,
            onMetodoSelect = { viewModel.seleccionarMetodoCalculo(it) },
            onContinuar = { viewModel.continuarAjusteMeta() },
            onDismiss = { viewModel.mostrarDialogoAjustarMeta(false) }
        )
    }

    if (state.mostrarDialogoPesoEstatura) {
        PesoEstaturaDialog(
            pesoInput = state.pesoInput,
            estaturaInput = state.estaturaInput,
            editandoPeso = state.editandoPeso,
            metaRecomendada = state.metaRecomendada,
            onCambiarModo = { viewModel.setEditandoPeso(it) },
            onPesoChange = { viewModel.onPesoInputChange(it) },
            onEstaturaChange = { viewModel.onEstaturaInputChange(it) },
            onGuardar = { viewModel.guardarMetaCalculada() },
            onAtras = {
                viewModel.mostrarDialogoPesoEstatura(false)
                viewModel.mostrarDialogoAjustarMeta(true)
            },
            onDismiss = { viewModel.mostrarDialogoPesoEstatura(false) }
        )
    }

    if (state.mostrarDialogoMetaManual) {
        MetaManualAguaDialog(
            metaInput = state.metaManualInput,
            vasos = state.vasosMetaManual,
            litros = state.litrosMetaManual,
            onAjustar = { viewModel.ajustarMetaManual(it) },
            onInputChange = { viewModel.onMetaManualInputChange(it) },
            onQuickSelect = { viewModel.setMetaManual(it) },
            onGuardar = { viewModel.guardarMetaManual() },
            onAtras = {
                viewModel.mostrarDialogoMetaManual(false)
                viewModel.mostrarDialogoAjustarMeta(true)
            },
            onDismiss = { viewModel.mostrarDialogoMetaManual(false) }
        )
    }

    if (state.mostrarDialogoCustomAmount) {
        CustomAmountAguaDialog(
            amount = state.customAmountTemporal,
            vasos = state.vasosCustom,
            onAjustar = { viewModel.ajustarCustomAmount(it) },
            onConfirmar = { viewModel.confirmarCustomAmount() },
            onDismiss = { viewModel.mostrarDialogoCustomAmount(false) }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BgMain
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BgMain)
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
                        text = "Hidratación Diaria",
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
                    text = "Hidratación Diaria",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = AguaAzulMedio,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = state.fraseMotivacional,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 20.dp),
                    textAlign = TextAlign.Center
                )

                // ── CARD PRINCIPAL AZUL ───────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(AguaAzulMedio, AguaAzulOscuro)
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "💧", fontSize = 52.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        val (valHoy, unitHoy) = state.formatWater(state.mlActuales)
                        val (valObj, unitObj) = state.formatWater(state.mlObjetivo)
                        val (valRest, unitRest) = state.formatWater(state.mlRestantes)

                        Text(
                            text = "$valHoy $unitHoy",
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "de $valObj $unitObj objetivo diario (${state.vasosActuales} vasos)",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            LinearProgressIndicator(
                                progress = { state.porcentaje },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = Color.White,
                                trackColor = Color.White.copy(alpha = 0.3f)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "${(state.porcentaje * 100).toInt()}%",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Te faltan $valRest $unitRest (≈${state.vasosRestantes} vasos)",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .clickable { viewModel.mostrarDialogoAjustarMeta(true) }
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Ajustar meta",
                                fontSize = 13.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── REGISTRO RÁPIDO ───────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = "Registro Rápido",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Toca para agregar agua que tomaste",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            BotonAgua(emoji = "1",  etiqueta = "250 ml",  subEtiqueta = "1 vaso",  modifier = Modifier.weight(1f)) { viewModel.agregarAgua(250) }
                            BotonAgua(emoji = "2",  etiqueta = "500 ml",  subEtiqueta = "2 vasos", modifier = Modifier.weight(1f)) { viewModel.agregarAgua(500) }
                            BotonAgua(emoji = "1L", etiqueta = "1.0 L", subEtiqueta = "Botella", modifier = Modifier.weight(1f)) { viewModel.agregarAgua(1000) }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, AguaBorde, RoundedCornerShape(12.dp))
                                .clickable { viewModel.mostrarDialogoCustomAmount(true) }
                                .padding(vertical = 12.dp)
                        ) {
                            Text(text = "+", fontSize = 16.sp, color = AguaAzulMedio, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Otra cantidad", fontSize = 14.sp, color = AguaAzulMedio, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── CONSEJOS ──────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .padding(20.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                tint = AguaAzulMedio,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Consejos de Hidratación",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        if (state.tipsHidratacion.isNotEmpty()) {
                            state.tipsHidratacion.forEach { tip ->
                                ConsejoBullet(texto = tip)
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        } else {
                            ConsejoBullet(texto = "💧 Toma 1 vaso al despertar para activar tu metabolismo")
                            Spacer(modifier = Modifier.height(10.dp))
                            ConsejoBullet(texto = "🧠 La hidratación mejora la concentración y memoria")
                            Spacer(modifier = Modifier.height(10.dp))
                            ConsejoBullet(texto = "⏰ Establece recordatorios cada 2 horas")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun BotonAgua(
    emoji: String,
    etiqueta: String,
    subEtiqueta: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AguaTarjetaBg)
            .clickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(AguaAzulClaro, AguaAzulMedio)
                    )
                )
        ) {
            Text(text = emoji, fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Black)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = etiqueta, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AguaAzulOscuro)
        Text(text = subEtiqueta, fontSize = 11.sp, color = TextSecondary)
    }
}

@Composable
fun ConsejoBullet(texto: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text(text = "●", fontSize = 10.sp, color = AguaAzulMedio, modifier = Modifier.padding(top = 3.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = texto, fontSize = 13.sp, color = TextSecondary, lineHeight = 18.sp)
    }
}
