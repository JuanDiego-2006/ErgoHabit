package com.knexus.ergohabit.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.knexus.ergohabit.features.auth.presentation.screens.LoginScreen
import com.knexus.ergohabit.features.posture.presentation.screens.PostureScreen

@Composable
fun GrafoNavegacion(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRuta.Login
    ) {
        composable<NavRuta.Login> {
            LoginScreen(
                onNavigateToRegister = {},
                onNavigateToHome = {
                    navController.navigate(NavRuta.Inicio) {
                        popUpTo(NavRuta.Login) { inclusive = true }
                    }
                }
            )
        }

        composable<NavRuta.Inicio> {
            PostureScreen()
        }
    }
}