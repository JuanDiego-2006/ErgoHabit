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
import com.knexus.ergohabit.ui.theme.*

data class NavItem(val label: String, val icon: ImageVector)

@Composable
fun BarraNavegacionInferior() {
    val items = listOf(
        NavItem("Inicio", Icons.Outlined.Home),
        NavItem("Tareas", Icons.Outlined.CheckBoxOutlineBlank),
        NavItem("Sensores", Icons.Outlined.ShowChart),
        NavItem("Progreso", Icons.Outlined.BarChart),
        NavItem("Perfil", Icons.Outlined.Person)
    )
    var selected by remember { mutableStateOf(0) }

    NavigationBar(
        containerColor = BgWhite, tonalElevation = 0.dp,
        modifier = Modifier.fillMaxWidth().navigationBarsPadding()
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selected == index,
                onClick = { selected = index },
                icon = { Icon(item.icon, contentDescription = item.label, modifier = Modifier.size(22.dp)) },
                label = { Text(item.label, fontSize = 11.sp, fontWeight = if (selected == index) FontWeight.Medium else FontWeight.Normal) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = GreenPrimary, selectedTextColor = GreenPrimary,
                    indicatorColor = GreenLight, unselectedIconColor = TextGray, unselectedTextColor = TextGray
                )
            )
        }
    }
}