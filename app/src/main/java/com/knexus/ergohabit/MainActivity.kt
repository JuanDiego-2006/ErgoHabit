package com.knexus.ergohabit

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.knexus.ergohabit.core.navigation.GrafoNavegacion
import com.knexus.ergohabit.core.session.SessionManager
import com.knexus.ergohabit.features.tareas.presentation.viewmodel.TareaViewModel
import com.knexus.ergohabit.ui.theme.ErgoHabitTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    private var currentIntent by mutableStateOf<Intent?>(null)

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        currentIntent = intent
    }

    private val lanzadorPermisos = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        if (permisos.all { it.value }) {
            println("ErgoHabit: Todos los permisos concedidos.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        currentIntent = intent

        val permisosASolicitar = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permisosASolicitar.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permisosASolicitar.add(Manifest.permission.FOREGROUND_SERVICE_SPECIAL_USE)
        }
        if (permisosASolicitar.isNotEmpty()) {
            lanzadorPermisos.launch(permisosASolicitar.toTypedArray())
        }

        setContent {
            ErgoHabitTheme {
                val navController = rememberNavController()

                // --- MANEJO GLOBAL DE REDIRECCIÓN POR NOTIFICACIONES ---
                LaunchedEffect(currentIntent) {
                    val intent = currentIntent
                    if (intent != null && (intent.hasExtra("frase") || intent.hasExtra("tareaCompletadaId"))) {
                        // Si el usuario está logueado, lo mandamos a Tareas
                        if (sessionManager.fetchAuthToken() != null) {
                            navController.navigate(com.knexus.ergohabit.core.navigation.NavRuta.Tareas()) {
                                launchSingleTop = true
                            }
                        }
                    }
                }
                // -------------------------------------------------------

                GrafoNavegacion(
                    navController = navController,
                    sessionManager = sessionManager
                )
            }
        }
    }
}