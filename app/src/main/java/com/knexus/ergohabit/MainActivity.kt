package com.knexus.ergohabit

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.rememberNavController
import com.knexus.ergohabit.core.navigation.GrafoNavegacion
import com.knexus.ergohabit.core.session.SessionManager
import com.knexus.ergohabit.features.posture.domain.GestorMonitoreoPostura
import com.knexus.ergohabit.features.posture.presentation.components.CamaraPosturaMonitor
import com.knexus.ergohabit.ui.theme.ErgoHabitTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var gestorMonitoreo: GestorMonitoreoPostura

    private val reactiveIntent = mutableStateOf<Intent?>(null)
    private val permisoCamaraState = mutableStateOf(false)

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        reactiveIntent.value = intent
    }

    private val lanzadorPermisos = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        permisoCamaraState.value = tienePermisoCamara()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        permisoCamaraState.value = tienePermisoCamara()
        reactiveIntent.value = intent

        setContent {
            ErgoHabitTheme {
                val context = LocalContext.current
                val lifecycleOwner = LocalLifecycleOwner.current
                val monitoreo by gestorMonitoreo.estado.collectAsState()
                val tienePermisoCamara by permisoCamaraState
                val currentReactiveIntent by reactiveIntent
                val navController = rememberNavController()

                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            permisoCamaraState.value = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
                }

                CamaraPosturaMonitor(
                    activo = monitoreo.activo,
                    tienePermisoCamara = tienePermisoCamara,
                    lifecycleOwner = lifecycleOwner,
                    onResultado = { gestorMonitoreo.actualizarCamara(it) },
                    onCamaraInactiva = { gestorMonitoreo.marcarCamaraInactiva() }
                )

                LaunchedEffect(currentReactiveIntent) {
                    val intent = currentReactiveIntent
                    if (intent != null) {
                        if (intent.hasExtra("frase") || intent.hasExtra("tareaCompletadaId") || intent.hasExtra("idTareaAlerta")) {
                            if (sessionManager.fetchAuthToken() != null) {
                                navController.navigate(com.knexus.ergohabit.core.navigation.NavRuta.Tareas()) {
                                    launchSingleTop = true
                                }
                            }
                        } else if (intent.hasExtra("irASueno") || intent.hasExtra("mostrarAlarmaSueno")) {
                            if (sessionManager.fetchAuthToken() != null) {
                                navController.navigate(com.knexus.ergohabit.core.navigation.NavRuta.Sueno) {
                                    launchSingleTop = true
                                }
                            }
                        } else if (intent.hasExtra("irANutricion")) {
                            if (sessionManager.fetchAuthToken() != null) {
                                navController.navigate(com.knexus.ergohabit.core.navigation.NavRuta.Nutricion) {
                                    launchSingleTop = true
                                }
                            }
                        }
                    }
                }

                GrafoNavegacion(
                    navController = navController,
                    sessionManager = sessionManager,
                    notificationIntent = currentReactiveIntent
                )
            }
        }

        window.decorView.post { solicitarPermisosEnTiempoDeEjecucion() }
    }

    private fun tienePermisoCamara(): Boolean =
        ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED

    private fun solicitarPermisosEnTiempoDeEjecucion() {
        val permisosASolicitar = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permisosASolicitar.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permisosASolicitar.add(Manifest.permission.ACTIVITY_RECOGNITION)
        }
        if (!tienePermisoCamara()) {
            permisosASolicitar.add(Manifest.permission.CAMERA)
        }
        if (permisosASolicitar.isNotEmpty()) {
            lanzadorPermisos.launch(permisosASolicitar.toTypedArray())
        }
    }
}
