package com.knexus.ergohabit.features.posture.presentation.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.knexus.ergohabit.features.posture.presentation.viewmodel.ActividadViewModel
import com.knexus.ergohabit.ui.theme.ActividadAmarillo
import com.knexus.ergohabit.ui.theme.ActividadAmarilloBg
import com.knexus.ergohabit.ui.theme.ActividadBotonVerde
import com.knexus.ergohabit.ui.theme.ActividadMorado
import com.knexus.ergohabit.ui.theme.ActividadMoradoBg
import com.knexus.ergohabit.ui.theme.ActividadNaranja
import com.knexus.ergohabit.ui.theme.ActividadNaranjaBg
import com.knexus.ergohabit.ui.theme.ActividadVerde
import com.knexus.ergohabit.ui.theme.BgMain
import com.knexus.ergohabit.ui.theme.GreenLight
import com.knexus.ergohabit.ui.theme.GreenPrimary
import com.knexus.ergohabit.ui.theme.TextPrimary
import com.knexus.ergohabit.ui.theme.TextSecondary

@Composable
fun ActividadScreen(
    viewModel: ActividadViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToConfigMeta: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val permisoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) viewModel.iniciarSensor()
        else viewModel.onPermisoDenegado()
    }

    fun alternarSensor() {
        if (state.sensorActivo) {
            viewModel.detenerSensor()
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val tienePermiso = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
            if (tienePermiso) viewModel.iniciarSensor()
            else permisoLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        } else {
            viewModel.iniciarSensor()
        }
    }

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
                    text = "Actividad Diaria",
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
                text = "Actividad Diaria",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = ActividadVerde,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Sigue moviéndote para alcanzar tus metas",
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 20.dp),
                textAlign = TextAlign.Center
            )

            // ── CARD PRINCIPAL ────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    // Círculo de progreso
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(200.dp)
                    ) {
                        val progresoColor = ActividadVerde
                        val trackColor = Color(0xFFE8EDE9)

                        Canvas(modifier = Modifier.size(200.dp)) {
                            val strokeWidth = 16.dp.toPx()
                            drawArc(
                                color = trackColor,
                                startAngle = -220f,
                                sweepAngle = 260f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                            drawArc(
                                color = progresoColor,
                                startAngle = -220f,
                                sweepAngle = 260f * state.porcentaje,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "👟", fontSize = 28.sp)
                            Text(
                                text = "${"%.2f".format(state.kmActuales)} km",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Text(
                                text = "de ${state.kmObjetivo} km",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = "${(state.porcentaje * 100).toInt()}%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ActividadVerde
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Stats row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Calorías
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(ActividadNaranjaBg)
                                .padding(16.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "🔥", fontSize = 22.sp)
                                Text(
                                    text = "${state.calorias}",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ActividadNaranja
                                )
                                Text(
                                    text = "kcal",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        // Racha días
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(ActividadMoradoBg)
                                .padding(16.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.EmojiEvents,
                                    contentDescription = null,
                                    tint = ActividadMorado,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    text = "${state.rachasDias}",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ActividadMorado
                                )
                                Text(
                                    text = "días",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ── SENSOR DE PASOS ───────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .padding(20.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (state.sensorActivo) ActividadVerde else Color(0xFFE8EDE9)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DirectionsWalk,
                                contentDescription = null,
                                tint = if (state.sensorActivo) Color.White else TextSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Sensor de actividad",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = when {
                                    !state.sensorDisponible -> "No disponible en este dispositivo"
                                    state.sensorActivo -> "Monitoreando tus pasos en tiempo real"
                                    else -> "Activa el sensor al caminar o correr"
                                },
                                fontSize = 12.sp,
                                color = if (state.sensorActivo) ActividadVerde else TextSecondary
                            )
                        }
                        Text(
                            text = if (state.sensorActivo) "ON" else "OFF",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (state.sensorActivo) ActividadVerde else TextSecondary
                        )
                    }

                    if (state.sensorActivo || state.pasosSesion > 0) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(ActividadVerde.copy(alpha = 0.08f))
                                    .padding(14.dp)
                            ) {
                                Text(
                                    text = "${state.pasosSesion}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ActividadVerde
                                )
                                Text(
                                    text = "pasos",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(GreenLight)
                                    .padding(14.dp)
                            ) {
                                Text(
                                    text = "%.2f".format(state.kmSesion),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GreenPrimary
                                )
                                Text(
                                    text = "km sesión",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    if (state.sensorActivo) {
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = ActividadVerde,
                            trackColor = Color(0xFFE8EDE9)
                        )
                    }

                    state.errorSensor?.let { error ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = error,
                            fontSize = 12.sp,
                            color = Color(0xFFD32F2F),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { alternarSensor() },
                            enabled = state.sensorDisponible && !state.isRegistrando,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (state.sensorActivo) Color(0xFF757575) else ActividadVerde
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = if (state.sensorActivo) "Detener" else "Iniciar",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = { viewModel.sincronizarSesion() },
                            enabled = state.kmSesion >= 0.01f && !state.isRegistrando,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = if (state.isRegistrando) "Guardando..." else "Guardar",
                                fontWeight = FontWeight.Bold,
                                color = GreenPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ── CONFIGURAR META ───────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .padding(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(GreenLight)
                        .clickable { onNavigateToConfigMeta() }
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = null,
                        tint = GreenPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Configurar meta",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Meta actual: ${state.kmObjetivo} km",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                    Icon(
                        imageVector = Icons.Outlined.TrackChanges,
                        contentDescription = null,
                        tint = GreenPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ── LOGROS RECIENTES ──────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(ActividadAmarilloBg)
                    .padding(20.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.EmojiEvents,
                            contentDescription = null,
                            tint = ActividadAmarillo,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Logros Recientes",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(ActividadAmarilloBg)
                        ) {
                            Text(text = "🔥", fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Racha de ${state.rachasDias} días",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Sigue así para mantenerla",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ── BANNER MOTIVACIONAL ───────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(ActividadBotonVerde)
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = state.mensajeBanner,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = state.sugerenciaBanner,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}