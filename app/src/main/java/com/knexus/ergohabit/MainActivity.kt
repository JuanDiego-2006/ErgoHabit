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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
// --- TUS CAMBIOS: IMPORTACIÓN DE COMPOSABLE EXTRA ---
import androidx.compose.runtime.*
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

    // --- TUS CAMBIOS: VARIABLE DE INTENT REACTIVO ---
    private var currentIntent by mutableStateOf<Intent?>(null)

    private val permisoCamaraState = mutableStateOf(false)

    // --- TUS CAMBIOS: IMPLEMENTACIÓN DE ONNEWINTENT ---
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        currentIntent = intent
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
        // --- TUS CAMBIOS: CAPTURA DEL INTENT INICIAL ---
        currentIntent = intent

        setContent {
            ErgoHabitTheme {
                val context = LocalContext.current
                val lifecycleOwner = LocalLifecycleOwner.current
                val monitoreo by gestorMonitoreo.estado.collectAsState()
                val tienePermisoCamara by permisoCamaraState
                val navController = rememberNavController()

                androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
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

                // --- TUS CAMBIOS: MANEJO GLOBAL DE REDIRECCIÓN POR NOTIFICACIONES ---
                LaunchedEffect(currentIntent) {
                    val intent = currentIntent
                    if (intent != null && (intent.hasExtra("frase") || intent.hasExtra("tareaCompletadaId") || intent.hasExtra("idTareaAlerta"))) {
                        if (sessionManager.fetchAuthToken() != null) {
                            navController.navigate(com.knexus.ergohabit.core.navigation.NavRuta.Tareas()) {
                                launchSingleTop = true
                            }
                        }
                    }
                }
                // -------------------------------------------------------------------

                GrafoNavegacion(
                    navController = navController,
                    sessionManager = sessionManager,
                    // --- TUS CAMBIOS: SE PASA EL INTENT AL GRAFO ---
                    notificationIntent = currentIntent
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