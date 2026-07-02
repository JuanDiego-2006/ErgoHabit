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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.knexus.ergohabit.features.posture.presentation.viewmodel.RetrasoSuenoViewModel
import com.knexus.ergohabit.ui.theme.SuenoAlertaRed
import com.knexus.ergohabit.ui.theme.SuenoPurple1
import com.knexus.ergohabit.ui.theme.TextPrimary
import com.knexus.ergohabit.ui.theme.TextSecondary

@Composable
fun RetrasoSuenoScreen(
    viewModel: RetrasoSuenoViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onConfirmar: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.success) {
        if (state.success) {
            onConfirmar()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f)),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Fondo clickeable para cerrar
        Box(modifier = Modifier.fillMaxSize().clickable { onNavigateBack() })

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .clickable(enabled = false) { } // Evitar que clics en el panel cierren
        ) {

            // Handle
            Box(
                modifier = Modifier
                    .size(width = 40.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Ajustar horario de sueño",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Esto ajustará permanentemente tu hora de dormir. Tu rendimiento puede verse afectado si duermes menos de lo recomendado.",
                fontSize = 13.sp,
                color = TextSecondary,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Horas a retrasar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Horas a retrasar",
                    fontSize = 14.sp,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${state.horasRetraso}h",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = SuenoPurple1
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Slider con botones (Rango 1-3 horas)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF0F0F0))
                        .clickable { viewModel.decrementarHoras() }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Remove,
                        contentDescription = "Reducir",
                        tint = SuenoPurple1,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Slider(
                    value = state.horasRetraso.toFloat(),
                    onValueChange = { valor ->
                        val nuevo = valor.toInt().coerceIn(0, 3)
                        val actual = state.horasRetraso
                        if (nuevo > actual) {
                            repeat(nuevo - actual) { viewModel.incrementarHoras() }
                        } else if (nuevo < actual) {
                            repeat(actual - nuevo) { viewModel.decrementarHoras() }
                        }
                    },
                    valueRange = 0f..3f,
                    steps = 2,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = SuenoPurple1,
                        activeTrackColor = SuenoPurple1,
                        inactiveTrackColor = Color(0xFFE0E0E0)
                    )
                )

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF0F0F0))
                        .clickable { viewModel.incrementarHoras() }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Aumentar",
                        tint = SuenoPurple1,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Alerta límite mínimo (Si se duerme 5h o menos)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (state.enLimiteMinimo) Color(0xFFFFF3E0) else Color(0xFFE8F5E9))
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (state.enLimiteMinimo) Icons.Outlined.Warning else Icons.Outlined.Check,
                            contentDescription = null,
                            tint = if (state.enLimiteMinimo) SuenoAlertaRed else Color(0xFF2E7D32),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Dormirás ${state.horasResultantes} horas",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (state.enLimiteMinimo) SuenoAlertaRed else Color(0xFF2E7D32)
                        )
                    }
                    if (state.enLimiteMinimo) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "⚠️ Estás en el límite mínimo. Tu rendimiento puede verse afectado.",
                            fontSize = 12.sp,
                            color = SuenoAlertaRed,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón confirmar
            Button(
                onClick = { viewModel.confirmar() },
                enabled = !state.isLoading,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SuenoPurple1),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Confirmar y ajustar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botón cancelar
            Button(
                onClick = { onNavigateBack() },
                enabled = !state.isLoading,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF5F5F5),
                    contentColor = TextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = "Cancelar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
