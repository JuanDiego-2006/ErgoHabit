package com.knexus.ergohabit.features.posture.presentation.screens

import android.content.Intent
import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.knexus.ergohabit.features.posture.presentation.components.BarraNavegacionInferior
import com.knexus.ergohabit.features.posture.presentation.components.FilaEstadisticas
import com.knexus.ergohabit.features.posture.presentation.components.HabitCard
import com.knexus.ergohabit.features.posture.presentation.components.TarjetaSensor
import com.knexus.ergohabit.features.posture.presentation.services.PostureForegroundService
import com.knexus.ergohabit.features.posture.presentation.viewmodel.PosturaViewModel
import com.knexus.ergohabit.ui.theme.BgMain
import com.knexus.ergohabit.ui.theme.GreenPrimary
import com.knexus.ergohabit.ui.theme.GreenProgress
import com.knexus.ergohabit.ui.theme.PurpleAccent
import com.knexus.ergohabit.ui.theme.TextPrimary
import com.knexus.ergohabit.ui.theme.TextSecondary

@Composable
fun PostureScreen(
    viewModel: PosturaViewModel = hiltViewModel(),
    onNavigateToHidratacion: () -> Unit = {},
    onNavigateToSueno: () -> Unit = {},
    onNavigateToActividad: () -> Unit = {},
    onNavigateToNutricion: () -> Unit = {}
) {
    val context = LocalContext.current
    val estado by viewModel.estadoUi.collectAsState()

    Scaffold(
        containerColor = BgMain,
        bottomBar = { BarraNavegacionInferior() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Buenos días, Carlos 👋",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 18.dp)
            )

            TarjetaSensor(
                isSensorActive = estado.estaMonitoreando,
                gradosInclinacion = estado.anguloPitch.toInt(),
                onToggleClick = {
                    viewModel.alternarMonitoreo()
                    val intent = Intent(context, PostureForegroundService::class.java)
                    if (!estado.estaMonitoreando) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(intent)
                        } else {
                            context.startService(intent)
                        }
                    } else {
                        context.stopService(intent)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
            FilaEstadisticas(vibraciones = estado.conteoVibraciones)
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "MICRO-HÁBITOS · 1 DE 4 COMPLETADOS",
                fontSize = 11.sp,
                color = TextSecondary,
                letterSpacing = 0.06.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            HabitCard(
                emoji = "💧",
                name = "Agua",
                meta = "Meta: 8 vasos",
                pct = 75,
                pctColor = GreenPrimary,
                progressColor = GreenProgress,
                completed = false,
                onClick = { onNavigateToHidratacion() }
            )
            HabitCard(
                emoji = "🌙",
                name = "Sueño",
                meta = "Meta: 8 horas",
                pct = 88,
                pctColor = PurpleAccent,
                progressColor = PurpleAccent,
                completed = true,
                onClick = { onNavigateToSueno() }
            )
            HabitCard(
                emoji = "🏃",
                name = "Ejercicio",
                meta = "Meta: 30 min",
                pct = 67,
                pctColor = GreenPrimary,
                progressColor = GreenProgress,
                completed = false,
                onClick = { onNavigateToActividad() }
            )
            HabitCard(
                emoji = "🍎",
                name = "Nutrición",
                meta = "Meta: 3 comidas",
                pct = 67,
                pctColor = GreenPrimary,
                progressColor = GreenProgress,
                completed = false,
                onClick = { onNavigateToNutricion() }
            )
        }
    }
}