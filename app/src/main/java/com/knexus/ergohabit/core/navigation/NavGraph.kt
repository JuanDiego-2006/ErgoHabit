package com.knexus.ergohabit.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.knexus.ergohabit.features.auth.presentation.screens.LoginScreen
import com.knexus.ergohabit.features.posture.presentation.screens.ActividadScreen
import com.knexus.ergohabit.features.posture.presentation.screens.ConfigHorarioScreen
import com.knexus.ergohabit.features.posture.presentation.screens.ConfigMetaScreen
import com.knexus.ergohabit.features.posture.presentation.screens.ConfigNutricionScreen
import com.knexus.ergohabit.features.posture.presentation.screens.HidratacionScreen
import com.knexus.ergohabit.features.posture.presentation.screens.NutricionScreen
import com.knexus.ergohabit.features.posture.presentation.screens.PostureScreen
import com.knexus.ergohabit.features.posture.presentation.screens.RegisterScreen
import com.knexus.ergohabit.features.posture.presentation.screens.RetrasoSuenoScreen
import com.knexus.ergohabit.features.posture.presentation.screens.SuenoScreen
import com.knexus.ergohabit.features.tareas.presentation.screens.TareaScreen

@Composable
fun GrafoNavegacion(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRuta.Login
    ) {
        composable<NavRuta.Login> {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(NavRuta.Register) },
                onNavigateToHome = {
                    navController.navigate(NavRuta.Inicio) {
                        popUpTo(NavRuta.Login) { inclusive = true }
                    }
                }
            )
        }

        composable<NavRuta.Register> {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(NavRuta.Login) {
                        popUpTo(NavRuta.Register) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(NavRuta.Inicio) {
                        popUpTo(NavRuta.Register) { inclusive = true }
                    }
                }
            )
        }

        composable<NavRuta.Inicio> {
            PostureScreen(
                navController = navController,
                onNavigateToHidratacion = { navController.navigate(NavRuta.Hidratacion) },
                onNavigateToSueno = { navController.navigate(NavRuta.Sueno) },
                onNavigateToActividad = { navController.navigate(NavRuta.Actividad) },
                onNavigateToNutricion = { navController.navigate(NavRuta.Nutricion) }
            )
        }

        composable<NavRuta.Hidratacion> {
            HidratacionScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable<NavRuta.Sueno> {
            SuenoScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToConfigHorario = { navController.navigate(NavRuta.ConfigHorario) },
                onNavigateToRetrasoSueno = { navController.navigate(NavRuta.RetrasoSueno) }
            )
        }

        composable<NavRuta.ConfigHorario> {
            ConfigHorarioScreen(
                onNavigateBack = { navController.popBackStack() },
                onGuardar = { navController.popBackStack() }
            )
        }

        composable<NavRuta.RetrasoSueno> {
            RetrasoSuenoScreen(
                onNavigateBack = { navController.popBackStack() },
                onConfirmar = { navController.popBackStack() }
            )
        }

        composable<NavRuta.Actividad> {
            ActividadScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToConfigMeta = { navController.navigate(NavRuta.ConfigMeta) }
            )
        }

        composable<NavRuta.ConfigMeta> {
            ConfigMetaScreen(
                onNavigateBack = { navController.popBackStack() },
                onGuardar = { navController.popBackStack() }
            )
        }

        composable<NavRuta.Nutricion> {
            NutricionScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToConfigNutricion = { navController.navigate(NavRuta.ConfigNutricion) }
            )
        }

        composable<NavRuta.ConfigNutricion> {
            ConfigNutricionScreen(
                onNavigateBack = { navController.popBackStack() },
                onComenzar = { navController.popBackStack() }
            )
        }

        composable<NavRuta.Tareas> {
            TareaScreen(navController = navController)
        }
    }
}