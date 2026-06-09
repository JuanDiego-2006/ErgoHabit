package com.knexus.ergohabit

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.compose.rememberNavController
import com.knexus.ergohabit.core.navigation.GrafoNavegacion
import com.knexus.ergohabit.ui.theme.ErgoHabitTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

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
                GrafoNavegacion(navController = navController)
            }
        }
    }
}