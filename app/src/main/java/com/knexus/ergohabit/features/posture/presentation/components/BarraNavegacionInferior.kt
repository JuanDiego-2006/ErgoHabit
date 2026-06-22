package com.knexus.ergohabit.features.posture.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.knexus.ergohabit.core.navigation.NavRuta
import com.knexus.ergohabit.ui.theme.*

data class NavItem(val label: String, val icon: ImageVector, val ruta: NavRuta)

@Composable
fun BarraNavegacionInferior(navController: NavHostController) {
    val items = listOf(
        NavItem("Inicio", Icons.Outlined.Home, NavRuta.Inicio),
        NavItem("Tareas", Icons.Outlined.CheckBoxOutlineBlank, NavRuta.Tareas()),
        NavItem("Sensores", Icons.Outlined.ShowChart, NavRuta.Sensores),
        NavItem("Progreso", Icons.Outlined.BarChart, NavRuta.Progreso),
        NavItem("Perfil", Icons.Outlined.Person, NavRuta.Perfil)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = BgWhite, tonalElevation = 0.dp,
        modifier = Modifier.fillMaxWidth().navigationBarsPadding()
    ) {
        items.forEach { item ->
            val selected = currentRoute?.contains(item.ruta::class.simpleName ?: "") == true
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(item.ruta) {
                            popUpTo(NavRuta.Inicio) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label, modifier = Modifier.size(22.dp)) },
                label = { Text(item.label, fontSize = 11.sp, fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = GreenPrimary, selectedTextColor = GreenPrimary,
                    indicatorColor = GreenLight, unselectedIconColor = TextGray, unselectedTextColor = TextGray
                )
            )
        }
    }
}
