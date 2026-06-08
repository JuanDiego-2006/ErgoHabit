import com.knexus.ergohabit.features.posture.presentation.screens.MainScreen

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.knexus.ergohabit.features.posture.presentation.screens.MainScreen
import com.knexus.ergohabit.features.posture.presentation.viewmodel.PosturaViewModel

/**
 * Grafo de navegación principal.
 */
@Composable
fun GrafoNavegacion(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRuta.Inicio
    ) {
        composable<NavRuta.Inicio> {
            val viewModel: PosturaViewModel = hiltViewModel()
            MainScreen(viewModel = viewModel)
        }

        // Aquí irían las demás pantallas de la aplicación
        // composable<NavRuta.Tareas> { ... }
        // composable<NavRuta.Perfil> { ... }
    }
}
