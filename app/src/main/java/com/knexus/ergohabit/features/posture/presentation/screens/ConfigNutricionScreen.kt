package com.knexus.ergohabit.features.posture.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.knexus.ergohabit.features.posture.presentation.viewmodel.ConfigNutricionViewModel
import com.knexus.ergohabit.ui.theme.BgMain
import com.knexus.ergohabit.ui.theme.GreenPrimary
import com.knexus.ergohabit.ui.theme.NutricionVerde
import com.knexus.ergohabit.ui.theme.TextGray
import com.knexus.ergohabit.ui.theme.TextPrimary
import com.knexus.ergohabit.ui.theme.TextSecondary

@Composable
fun ConfigNutricionScreen(
    viewModel: ConfigNutricionViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onComenzar: () -> Unit = {}
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
                    text = "Nutrición",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            // ── EMOJI PLATO ───────────────────────────────
            Text(text = "🍽️", fontSize = 56.sp)

            Spacer(modifier = Modifier.height(16.dp))

            // ── TÍTULO ────────────────────────────────────
            Text(
                text = "Configura tu Nutrición",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = NutricionVerde,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Establece los horarios ideales para tus comidas. Te recordaremos 30 minutos antes.",
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ── CARD HORARIOS ─────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "Personaliza tus Horarios",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Ajusta según tu rutina diaria",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 2.dp, bottom = 20.dp)
                    )

                    // Desayuno
                    FilaHorarioComida(
                        emoji = "🥐",
                        label = "DESAYUNO",
                        valor = state.horaDesayuno,
                        onValueChange = { viewModel.onHoraDesayunoChange(it) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Comida
                    FilaHorarioComida(
                        emoji = "🍽️",
                        label = "COMIDA",
                        valor = state.horaComida,
                        onValueChange = { viewModel.onHoraComidaChange(it) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Cena
                    FilaHorarioComida(
                        emoji = "🌙",
                        label = "CENA",
                        valor = state.horaCena,
                        onValueChange = { viewModel.onHoraCenaChange(it) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── BOTÓN COMENZAR ────────────────────────────
            Button(
                onClick = {
                    viewModel.guardar()
                    onComenzar()
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = "Comenzar a Usar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}

// ── COMPONENTE FILA HORARIO ───────────────────────────────
@Composable
fun FilaHorarioComida(
    emoji: String,
    label: String,
    valor: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = emoji, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                letterSpacing = 0.08.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = valor,
                onValueChange = { nuevoValor -> onValueChange(nuevoValor) },
                placeholder = {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = TextGray,
                        modifier = Modifier.size(18.dp)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = GreenPrimary,
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedContainerColor = Color(0xFFF5F5F5),
                    cursorColor = GreenPrimary
                ),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE8F5EC))
                    .clickable { }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "Configurar",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = GreenPrimary
                )
            }
        }
    }
}