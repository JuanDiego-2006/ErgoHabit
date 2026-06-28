package com.knexus.ergohabit.features.posture.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.knexus.ergohabit.ui.theme.AguaAzulClaro
import com.knexus.ergohabit.ui.theme.AguaAzulMedio
import com.knexus.ergohabit.ui.theme.AguaAzulOscuro
import com.knexus.ergohabit.ui.theme.AguaBorde
import com.knexus.ergohabit.ui.theme.AguaTarjetaBg
import com.knexus.ergohabit.ui.theme.BgMain
import com.knexus.ergohabit.ui.theme.TextPrimary
import com.knexus.ergohabit.ui.theme.TextSecondary

@Composable
fun HidratacionScreen(
    viewModel: HidratacionViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
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
                text = "Mantente hidratado para rendir al máximo",
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
                    Text(
                        text = "${state.mlActuales} ml",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "de ${state.mlObjetivo} ml objetivo diario (${state.vasosObjetivo} vasos)",
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
                            text = "${state.porcentajeTexto}%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (state.metaCumplida) {
                            "¡Meta diaria cumplida! (${state.mlActuales} ml registrados)"
                        } else {
                            "Te faltan ${state.mlRestantes} ml (≈${state.vasosRestantes} vasos)"
                        },
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
                            .clickable { }
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
                        BotonAgua(emoji = "1",  etiqueta = "250ml",  subEtiqueta = "1 vaso",  modifier = Modifier.weight(1f)) { if (!state.isRegistrando) viewModel.agregarAgua(250) }
                        BotonAgua(emoji = "2",  etiqueta = "500ml",  subEtiqueta = "2 vasos", modifier = Modifier.weight(1f)) { if (!state.isRegistrando) viewModel.agregarAgua(500) }
                        BotonAgua(emoji = "1L", etiqueta = "1000ml", subEtiqueta = "Botella", modifier = Modifier.weight(1f)) { if (!state.isRegistrando) viewModel.agregarAgua(1000) }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, AguaBorde, RoundedCornerShape(12.dp))
                            .clickable { }
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
                    ConsejoBullet(texto = "💧 Toma 1 vaso al despertar para activar tu metabolismo")
                    Spacer(modifier = Modifier.height(10.dp))
                    ConsejoBullet(texto = "🧠 La hidratación mejora la concentración y memoria")
                    Spacer(modifier = Modifier.height(10.dp))
                    ConsejoBullet(texto = "⏰ Establece recordatorios cada 2 horas")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
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