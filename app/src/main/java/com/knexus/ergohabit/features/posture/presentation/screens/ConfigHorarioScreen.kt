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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.knexus.ergohabit.features.posture.presentation.viewmodel.ConfigHorarioViewModel
import com.knexus.ergohabit.ui.theme.BgMain
import com.knexus.ergohabit.ui.theme.GreenLight
import com.knexus.ergohabit.ui.theme.GreenPrimary
import com.knexus.ergohabit.ui.theme.OrangeAccent
import com.knexus.ergohabit.ui.theme.SuenoPurple1
import com.knexus.ergohabit.ui.theme.TextPrimary
import com.knexus.ergohabit.ui.theme.TextSecondary

@Composable
fun ConfigHorarioScreen(
    viewModel: ConfigHorarioViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onGuardar: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgMain)
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
                    text = "Gestión de Sueño",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── CARD PRINCIPAL ────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Color.White)
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {

                // Emoji amanecer
                Text(text = "🌅", fontSize = 48.sp)

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "¡Bienvenido a ErgoHabit!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Para ayudarte a dormir mejor, necesitamos saber a qué hora te levantas normalmente.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "¿A QUÉ HORA TE DESPIERTAS?",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary,
                    letterSpacing = 0.06.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ── SELECTOR DE HORA ──────────────────────
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Horas
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        BotonReloj(icono = true) { viewModel.incrementarHoras() }
                        Spacer(modifier = Modifier.height(8.dp))
                        CajaHora(valor = state.horas.toString().padStart(2, '0'))
                        Spacer(modifier = Modifier.height(8.dp))
                        BotonReloj(icono = false) { viewModel.decrementarHoras() }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "HORAS",
                            fontSize = 10.sp,
                            color = TextSecondary,
                            letterSpacing = 0.06.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Separador
                    Text(
                        text = ":",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = SuenoPurple1,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    // Minutos
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        BotonReloj(icono = true) { viewModel.incrementarMinutos() }
                        Spacer(modifier = Modifier.height(8.dp))
                        CajaHora(valor = state.minutos.toString().padStart(2, '0'))
                        Spacer(modifier = Modifier.height(8.dp))
                        BotonReloj(icono = false) { viewModel.decrementarMinutos() }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "MINUTOS",
                            fontSize = 10.sp,
                            color = TextSecondary,
                            letterSpacing = 0.06.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── HORARIO RECOMENDADO ───────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(GreenLight)
                        .padding(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "📊", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Tu horario recomendado",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = GreenPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Dormir",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = state.horaDormir,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = SuenoPurple1
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Icon(
                                imageVector = Icons.Outlined.ArrowForward,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Despertar",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = state.horaDespertar,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = OrangeAccent
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "8 horas de sueño · Puedes ajustarlo después",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── BOTÓN GUARDAR ─────────────────────────
                Button(
                    onClick = {
                        viewModel.guardarHorario()
                        onGuardar()
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Guardar y continuar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ── COMPONENTE BOTÓN + / - ────────────────────────────────
@Composable
fun BotonReloj(icono: Boolean, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .border(1.dp, SuenoPurple1.copy(alpha = 0.3f), CircleShape)
            .clickable { onClick() }
    ) {
        Icon(
            imageVector = if (icono) Icons.Outlined.Add else Icons.Outlined.Remove,
            contentDescription = null,
            tint = SuenoPurple1,
            modifier = Modifier.size(18.dp)
        )
    }
}

// ── COMPONENTE CAJA HORA ──────────────────────────────────
@Composable
fun CajaHora(valor: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(width = 90.dp, height = 80.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, SuenoPurple1.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .background(Color(0xFFF8F7FF))
    ) {
        Text(
            text = valor,
            fontSize = 40.sp,
            fontWeight = FontWeight.Black,
            color = SuenoPurple1
        )
    }
}