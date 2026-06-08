package com.knexus.ergohabit.features.posture.presentation.screens

import android.content.Intent
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knexus.ergohabit.ui.theme.*
import com.knexus.ergohabit.features.posture.presentation.components.*
import com.knexus.ergohabit.features.posture.presentation.services.PostureForegroundService

@Composable
fun PostureScreen() {
    val context = LocalContext.current
    // Aquí es donde el ViewModel entrará en el futuro. Por ahora usamos un estado local.
    var isSensorActive by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BgMain,
        bottomBar = { BarraNavegacionInferior() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Buenos días, Carlos 👋",
                fontSize = 26.sp, fontWeight = FontWeight.Black,
                color = TextPrimary, modifier = Modifier.padding(bottom = 18.dp)
            )

            // Usamos tu componente limpio y le pasamos la lógica
            TarjetaSensor(
                isSensorActive = isSensorActive,
                gradosInclinacion = 22,
                onToggleClick = {
                    isSensorActive = !isSensorActive
                    val intent = Intent(context, PostureForegroundService::class.java)
                    if (isSensorActive) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(intent)
                        } else {
                            context.startService(intent)
                        }
                    } else {
                        context.stopService(intent)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Aquí llamarías a tu componente de StatsRow
            // TarjetaEstadisticaRow()

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "MICRO-HÁBITOS · 1 DE 4 COMPLETADOS",
                fontSize = 11.sp, color = TextSecondary, letterSpacing = 0.06.sp,
                fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 12.dp)
            )

            // Aquí llamarías a tus componentes HabitCard
            // TarjetaHabito(emoji = "💧", name = "Agua", ...)
        }
    }
}