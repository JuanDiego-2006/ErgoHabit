package com.knexus.ergohabit.features.posture.presentation.screens

import androidx.activity.compose.BackHandler
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.knexus.ergohabit.features.posture.presentation.components.BarraNavegacionInferior
import com.knexus.ergohabit.features.posture.presentation.viewmodel.SuenoViewModel
import com.knexus.ergohabit.ui.theme.*

@Composable
fun SuenoScreen(
    navController: NavHostController,
    viewModel: SuenoViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToConfigHorario: () -> Unit = {},
    onNavigateToRetrasoSueno: () -> Unit = {},
    notificationIntent: android.content.Intent? = null
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }
    val snackbarHostState = remember { SnackbarHostState() }
    
    val alarmSound = remember { RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM) }
    val ringtone = remember { 
        RingtoneManager.getRingtone(context, alarmSound).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                isLooping = true
            }
            audioAttributes = android.media.AudioAttributes.Builder()
                .setUsage(android.media.AudioAttributes.USAGE_ALARM)
                .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        }
    }

    LaunchedEffect(state.error, state.successMessage) {
        state.error?.let { snackbarHostState.showSnackbar(it); viewModel.clearMessages() }
        state.successMessage?.let { snackbarHostState.showSnackbar(it); viewModel.clearMessages() }
    }

    LaunchedEffect(notificationIntent) {
        if (notificationIntent?.hasExtra("mostrarAlarmaSueno") == true) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            nm.cancel(201)
            viewModel.mostrarAlarma(true)
            notificationIntent.removeExtra("mostrarAlarmaSueno")
        }
    }

    LaunchedEffect(state.mostrarAlarma, state.reproducirSonido) {
        if (state.mostrarAlarma && state.reproducirSonido) {
            ringtone?.play()
            val pattern = longArrayOf(0, 500, 500)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern, 0)
            }
        } else {
            ringtone?.stop()
            vibrator.cancel()
        }
    }

    DisposableEffect(Unit) {
        onDispose { ringtone?.stop(); vibrator.cancel() }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = BgMain,
            bottomBar = { BarraNavegacionInferior(navController) },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                // ── HEADER ────────────────────────────────────────
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Box(Modifier.size(38.dp).clip(CircleShape).background(Color.White).clickable { onNavigateBack() }, contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.ArrowBackIosNew, null, tint = TextPrimary, modifier = Modifier.size(18.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("MICRO-HÁBITO", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                        Text("Gestión de Sueño", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }

                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Spacer(Modifier.height(8.dp))
                    Text("Gestión de Sueño", fontSize = 24.sp, fontWeight = FontWeight.Black, color = SuenoPurple1, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                    Text("Tu descanso es clave para el rendimiento académico", fontSize = 13.sp, color = TextSecondary, modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 20.dp), textAlign = TextAlign.Center)

                    // ── CARD PRINCIPAL MORADO ──
                    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Brush.verticalGradient(listOf(SuenoPurple1, SuenoPurple2))).padding(24.dp)) {
                        Column(Modifier.fillMaxWidth()) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Última Noche", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                                Icon(Icons.Outlined.Bedtime, null, tint = Color.White, modifier = Modifier.size(22.dp))
                            }
                            Spacer(Modifier.height(14.dp))
                            Text("${state.horasDormidas}h", fontSize = 48.sp, fontWeight = FontWeight.Black, color = Color.White)
                            Text("de ${state.horasRecomendadas.toInt()}h recomendadas", fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
                            Spacer(Modifier.height(16.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                LinearProgressIndicator(
                                    progress = { state.porcentaje },
                                    modifier = Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp)),
                                    color = Color.White,
                                    trackColor = Color.White.copy(alpha = 0.25f)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("${(state.porcentaje * 100).toInt()}%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("Calidad", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                                    Text(state.calidad, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                                Box(Modifier.clip(RoundedCornerShape(20.dp)).border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(20.dp)).padding(horizontal = 14.dp, vertical = 6.dp)) {
                                    Text("Buen descanso", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // ── ALERTA IMPACTO SUEÑO ──
                    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(SuenoAlertaBg).padding(18.dp)) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.Outlined.LightMode, null, tint = SuenoAlertaRed, modifier = Modifier.size(20.dp).padding(top = 2.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Impacto del Sueño Insuficiente", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SuenoAlertaRed)
                                Text("Dormir menos de 7-8 horas reduce significativamente la consolidación de memoria, aumenta el cortisol y disminuye tu capacidad para resolver problemas técnicos complejos.", fontSize = 13.sp, color = SuenoAlertaRed, lineHeight = 19.sp)
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // ── HORARIO DE SUEÑO ──
                    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Color.White).padding(20.dp)) {
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.Schedule, null, tint = PurpleAccent, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Tu Horario de Sueño", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                                Icon(
                                    imageVector = if (state.notificacionesHabilitadas) Icons.Outlined.Notifications else Icons.Outlined.NotificationsOff,
                                    contentDescription = "Toggle Notificaciones",
                                    tint = if (state.notificacionesHabilitadas) PurpleAccent else Color.Gray,
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clickable { viewModel.toggleNotificaciones() }
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Box(Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(SuenoHorarioBg).padding(16.dp)) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Outlined.Bedtime, null, tint = SuenoPurple1, modifier = Modifier.size(22.dp))
                                        Text("Dormir", fontSize = 11.sp, color = TextSecondary)
                                        Text(formatearParaDisplay(state.horaDormir), fontSize = 26.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                        Text("Recordatorio 5 min antes", fontSize = 10.sp, color = TextSecondary, textAlign = TextAlign.Center, lineHeight = 14.sp)
                                    }
                                }
                                Box(Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(SuenoOrangeBg).padding(16.dp)) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Outlined.WbSunny, null, tint = OrangeAccent, modifier = Modifier.size(22.dp))
                                        Text("Despertar", fontSize = 11.sp, color = TextSecondary)
                                        Text(formatearParaDisplay(state.horaDespertar), fontSize = 26.sp, fontWeight = FontWeight.Black, color = OrangeAccent)
                                        Text(if (state.alarmaActivada) "🔔 Alarma activada" else "Sin alarma", fontSize = 10.sp, color = TextSecondary)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(SuenoVerdeBg).padding(14.dp)) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                        Icon(Icons.Outlined.Check, null, tint = SuenoVerdeText, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("${state.horasPlanificadas.toInt()} horas planificadas", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SuenoVerdeText)
                                    }
                                    Text("✅ Cumples con las ${state.horasRecomendadas.toInt()}h recomendadas", fontSize = 12.sp, color = SuenoVerdeText, textAlign = TextAlign.Center)
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).border(1.dp, SuenoGrisBorde, RoundedCornerShape(12.dp)).clickable { onNavigateToConfigHorario() }.padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Outlined.Bedtime, null, tint = SuenoPurple1, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(6.dp))
                                        Text("Configurar horario", fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                                    }
                                }
                                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).border(1.dp, SuenoPurple1, RoundedCornerShape(12.dp)).clickable { onNavigateToRetrasoSueno() }.padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Outlined.Schedule, null, tint = SuenoPurple1, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(6.dp))
                                        Text("Retrasar sueño", fontSize = 13.sp, color = SuenoPurple1, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── CONSEJOS ──
                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Color.White).padding(20.dp)) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.SingleBed, null, tint = SuenoPurple1, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Consejos para Mejor Sueño", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            SuenoConsejoBullet("Evita pantallas 1 hora antes de dormir (luz azul)")
                            SuenoConsejoBullet("Mantén un horario constante, incluso los fines de semana")
                            SuenoConsejoBullet("Evita cafeína después de las 4 PM")
                            SuenoConsejoBullet("Crea un ambiente oscuro, fresco y silencioso")
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        if (state.mostrarAlarma) {
            BackHandler { }
            AlarmaSuenoOverlay(state.horaDespertar) { viewModel.registrarDespertar() }
        }
    }
}

@Composable
fun AlarmaSuenoOverlay(hora: String, onDespertar: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Color.White).padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text("⏰", fontSize = 80.sp)
            Spacer(modifier = Modifier.height(24.dp))
            Text("¡Es hora de despertar!", fontSize = 28.sp, fontWeight = FontWeight.Black, color = OrangeAccent, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Text(formatearParaDisplay(hora), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Buenos días! Tu jornada de estudio comienza ahora", fontSize = 15.sp, color = TextSecondary, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(modifier = Modifier.height(48.dp))
            Button(onClick = onDespertar, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = SuenoVerdeText)) {
                Icon(Icons.Outlined.WbSunny, null); Spacer(Modifier.width(8.dp)); Text("Ya me levanté", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(40.dp))
            Text("💡 Tip: Mantener un horario constante mejora tu rendimiento académico", fontSize = 12.sp, color = TextSecondary, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun SuenoConsejoBullet(texto: String) {
    Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(vertical = 5.dp)) {
        Text("●", fontSize = 10.sp, color = SuenoPurple1, modifier = Modifier.padding(top = 3.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = texto, fontSize = 13.sp, color = TextSecondary, lineHeight = 18.sp)
    }
}

private fun formatearParaDisplay(horario: String): String {
    // ELIMINACIÓN DE PARPADEO: Si el dato es inicial o cero, devolvemos vacío para que no salte el texto "Sin establecer"
    if (horario.isBlank() || horario == "--:--" || horario == "00:00" || horario == "00:00 AM") return ""
    
    val clean = horario.trim().uppercase()
    
    if (clean == "SIN ESTABLECER") return "Sin establecer"

    if (clean.contains("AM") || clean.contains("PM")) return if (clean.startsWith("00:00")) clean.replace("00:00", "12:00") else clean
    return try {
        val p = clean.split(":")
        var h = p[0].toInt()
        val m = p[1].take(2).toInt()
        val suffix = if (h >= 12) "PM" else "AM"
        if (h > 12) h -= 12
        if (h == 0) h = 12
        String.format(java.util.Locale.getDefault(), "%02d:%02d %s", h, m, suffix)
    } catch (e: Exception) { horario }
}
