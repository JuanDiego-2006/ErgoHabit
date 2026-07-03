package com.knexus.ergohabit.features.posture.presentation.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import java.util.Calendar
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import com.knexus.ergohabit.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
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
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(estado.error) {
        estado.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }
    
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
        bottomBar = { BarraNavegacionInferior(navController) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                val hora = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                val saludoBase = if (hora in 6..18) "Buenos días" else "Buenas noches"
                val saludo = if (estado.nombreUsuario.isNotBlank()) {
                    "$saludoBase, ${estado.nombreUsuario} 👋"
                } else {
                    "$saludoBase 👋"
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = saludo,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    
                    if (estado.cargandoDashboard) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        IconButton(onClick = { viewModel.refrescarDashboard() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refrescar")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

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
                
                // Si no hay hábitos y no está cargando, mostrar mensaje de error o vacío
                if (estado.resumenHabitos.isEmpty() && !estado.cargandoDashboard) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No se pudo cargar la información", color = TextGray)
                        Button(
                            onClick = { viewModel.refrescarDashboard() },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
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
