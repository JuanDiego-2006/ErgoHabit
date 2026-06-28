package com.knexus.ergohabit.features.posture.presentation.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
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
    navController: NavHostController,
    viewModel: PosturaViewModel = hiltViewModel(),
    onNavigateToHidratacion: () -> Unit = {},
    onNavigateToSueno: () -> Unit = {},
    onNavigateToActividad: () -> Unit = {},
    onNavigateToNutricion: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val estado by viewModel.estadoUi.collectAsState()
    var tienePermisoCamara by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }

    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        tienePermisoCamara = concedido
        if (concedido && !estado.estaMonitoreando) {
            alternarMonitoreo(context, viewModel, estado.estaMonitoreando)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refrescarDashboard()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val navegacionHabitos = mapOf(
        "Agua" to onNavigateToHidratacion,
        "Sueño" to onNavigateToSueno,
        "Ejercicio" to onNavigateToActividad,
        "Nutrición" to onNavigateToNutricion
    )

    Scaffold(
        containerColor = BgMain,
        bottomBar = { BarraNavegacionInferior(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            val saludo = if (estado.nombreUsuario.isNotBlank()) {
                "Buenos días, ${estado.nombreUsuario} 👋"
            } else {
                "Buenos días 👋"
            }

            Text(
                text = saludo,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 18.dp)
            )

            TarjetaSensor(
                isSensorActive = estado.estaMonitoreando,
                gradosInclinacion = estado.gradosDisplay,
                esCorrectaGlobal = estado.esCorrecta,
                mensajeCamara = estado.mensajeCamara,
                alertaPorCamara = estado.alertaPorCamara,
                camaraActiva = estado.camaraActiva,
                onToggleClick = {
                    if (!estado.estaMonitoreando && !tienePermisoCamara) {
                        permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
                        return@TarjetaSensor
                    }
                    alternarMonitoreo(context, viewModel, estado.estaMonitoreando)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
            FilaEstadisticas(
                vibraciones = estado.conteoVibraciones,
                habitosCompletados = estado.habitosCompletados,
                habitosTotal = estado.habitosTotal,
                rachaDias = estado.rachaDias
            )
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "MICRO-HÁBITOS · ${estado.habitosCompletados} DE ${estado.habitosTotal} COMPLETADOS",
                fontSize = 11.sp,
                color = TextSecondary,
                letterSpacing = 0.06.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            estado.resumenHabitos.forEach { habito ->
                val pctColor = when (habito.nombre) {
                    "Sueño" -> PurpleAccent
                    else -> GreenPrimary
                }
                val progressColor = when (habito.nombre) {
                    "Sueño" -> PurpleAccent
                    else -> GreenProgress
                }

                HabitCard(
                    emoji = habito.emoji,
                    name = habito.nombre,
                    meta = habito.meta,
                    pct = habito.pct,
                    pctColor = pctColor,
                    progressColor = progressColor,
                    completed = habito.completado,
                    onClick = { navegacionHabitos[habito.nombre]?.invoke() }
                )
            }
        }
    }
}

private fun alternarMonitoreo(
    context: android.content.Context,
    viewModel: PosturaViewModel,
    estaMonitoreando: Boolean
) {
    viewModel.alternarMonitoreo()
    val intent = Intent(context, PostureForegroundService::class.java)
    if (!estaMonitoreando) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    } else {
        context.stopService(intent)
    }
}
