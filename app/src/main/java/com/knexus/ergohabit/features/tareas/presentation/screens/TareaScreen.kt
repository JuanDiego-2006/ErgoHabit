package com.knexus.ergohabit.features.tareas.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.knexus.ergohabit.features.posture.presentation.components.BarraNavegacionInferior
import com.knexus.ergohabit.features.tareas.presentation.components.*
import com.knexus.ergohabit.features.tareas.presentation.viewmodel.TareaViewModel
import com.knexus.ergohabit.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareaScreen(
    navController: NavHostController,
    viewModel: TareaViewModel = hiltViewModel(),
    mostrarCompletadoInicial: Boolean = false,
    notificationIntent: android.content.Intent? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    val snackbarHostState = remember { SnackbarHostState() }

    // --- MANEJO DE INTENT DE NOTIFICACIÓN ---
    LaunchedEffect(notificationIntent) {
        notificationIntent?.let { intent ->
            val frase = intent.getStringExtra("frase")
            val accion = intent.getStringExtra("accion")
            val idTareaAlerta = intent.getIntExtra("idTareaAlerta", -1)
            val tareaCompletadaId = intent.getIntExtra("tareaCompletadaId", -1)

            if (idTareaAlerta != -1) {
                // Prioridad a la alerta de salud
                viewModel.mostrarAlertaDesdeNotificacion(
                    frase ?: "¡Momento de salud! 🧘", 
                    accion ?: "Es hora de estirar", 
                    idTareaAlerta
                )
                intent.removeExtra("idTareaAlerta")
                intent.removeExtra("frase")
                intent.removeExtra("accion")
            } else if (tareaCompletadaId != -1) {
                viewModel.prepararPantallaCompletado(tareaCompletadaId)
                intent.removeExtra("tareaCompletadaId")
            }
        }
    }

    // --- MANEJO DE MENSAJES (ÉXITO Y ERROR) ---
    LaunchedEffect(uiState.error, uiState.successMessage) {
        if (uiState.error != null) {
            snackbarHostState.showSnackbar(uiState.error!!)
            viewModel.clearMessages()
        }
        if (uiState.successMessage != null) {
            snackbarHostState.showSnackbar(uiState.successMessage!!)
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(mostrarCompletadoInicial) {
        if (mostrarCompletadoInicial) {
            viewModel.mostrarSheetCompletado(true)
        }
    }

    // ── DIÁLOGOS Y BOTTOM SHEETS ──
    if (uiState.mostrarRecordatorioEstiramiento) {
        AlertDialog(
            onDismissRequest = { viewModel.descartarRecordatorio() },
            title = { Text("¡Tiempo de estirar! 🧘", fontWeight = FontWeight.Bold) },
            text = { Text("Has estado trabajando por 25 minutos. Tómate un breve respiro para estirar tu espalda.") },
            confirmButton = {
                Button(onClick = { viewModel.descartarRecordatorio() }, colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)) {
                    Text("¡Entendido!", color = Color.White)
                }
            },
            shape = RoundedCornerShape(24.dp), containerColor = BgWhite
        )
    }

    if (uiState.mostrarSheetNuevaTarea) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.mostrarSheetNuevaTarea(false) },
            sheetState = sheetState, containerColor = Color.White,
            dragHandle = { BottomSheetDefaults.DragHandle(color = TextGray.copy(alpha = 0.3f)) }
        ) {
            SheetNuevaTarea(
                titulo = uiState.nuevoTitulo,
                onTituloChange = { viewModel.onTituloCambiado(it) },
                categoriasNombres = listOf("Académica", "Bienestar", "Laboral", "Enfoque Profundo", "Personal"),
                categoriaSeleccionada = uiState.nuevaCategoriaNombre,
                onCategoriaSelect = { viewModel.onCategoriaSeleccionada(it) },
                duracionMinutos = uiState.nuevaDuracion,
                onDuracionClick = { viewModel.mostrarSelectorDuracion(true) },
                onAgregarClick = { viewModel.agregarTarea() },
                onDismiss = { viewModel.mostrarSheetNuevaTarea(false) }
            )
        }
    }

    if (uiState.mostrarSelectorDuracion) {
        SelectorDuracionDialog(
            input = uiState.nuevaDuracionInput,
            onNumeroClick = { viewModel.onNumeroPresionado(it) },
            onBorrarClick = { viewModel.onBorrarPresionado() },
            onConfirmar = { viewModel.confirmarDuracion() },
            onDismiss = { viewModel.mostrarSelectorDuracion(false) }
        )
    }

    if (uiState.mostrarSheetCompletado) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.mostrarSheetCompletado(false) },
            sheetState = rememberModalBottomSheetState(),
            containerColor = Color.White,
            dragHandle = { BottomSheetDefaults.DragHandle(color = TextGray.copy(alpha = 0.3f)) }
        ) {
            SheetTareaFin(
                tituloTarea = uiState.tareaSeleccionada?.titulo ?: "",
                onCompletar = { viewModel.completarTarea() },
                onMasTiempo = { viewModel.mostrarSheetMasTiempo(true) }
            )
        }
    }

    if (uiState.mostrarSheetMasTiempo) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.mostrarSheetMasTiempo(false) },
            sheetState = rememberModalBottomSheetState(),
            containerColor = Color.White,
            dragHandle = { BottomSheetDefaults.DragHandle(color = TextGray.copy(alpha = 0.3f)) }
        ) {
            SheetMasTiempo(
                onConfirmar = { viewModel.agregarMasTiempo(it) },
                onCancelar = { viewModel.mostrarSheetMasTiempo(false) }
            )
        }
    }

    if (uiState.mostrarMensajeExito) {
        MensajeExitoDialog(mensaje = uiState.mensajeExito, onDismiss = { viewModel.descartarMensajeExito() })
    }

    if (uiState.mostrarAlertaSalud) {
        uiState.alertaSaludInfo?.let { info ->
            AlertaSaludDialog(
                frase = info.frase,
                accion = info.accion,
                onDismiss = { viewModel.descartarAlertaSalud() }
            )
        }
    }

    // ── ESTRUCTURA PRINCIPAL ──
    Scaffold(
        containerColor = BgMain,
        bottomBar = { BarraNavegacionInferior(navController) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp)) {
            // Header
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "FOCO ACADÉMICO", style = MaterialTheme.typography.labelSmall, color = TextGray, letterSpacing = 1.sp)
                    Text(text = "Mis Tareas", style = MaterialTheme.typography.headlineMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = { viewModel.mostrarSheetNuevaTarea(true) }, modifier = Modifier.clip(CircleShape).background(TextPrimary)) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir", tint = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = uiState.tareasEstado?.totalPendientesText ?: "CARGANDO TAREAS...", style = MaterialTheme.typography.labelMedium, color = TextGray, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                uiState.tareasEstado?.pendientes?.let { pendientes ->
                    items(pendientes, key = { it.id }) { tarea ->
                        ItemTarea(
                            tarea = tarea, 
                            isSelected = uiState.tareaSeleccionada?.id == tarea.id, 
                            onClick = { viewModel.seleccionarTarea(tarea) },
                            onDeleteClick = { viewModel.eliminarTarea(tarea.id) }
                        )
                    }
                }
                item(key = "spacer_completadas") {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = uiState.tareasEstado?.totalCompletadasText ?: "", style = MaterialTheme.typography.labelMedium, color = TextGray, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                uiState.tareasEstado?.completadas?.let { completadas ->
                    items(completadas, key = { it.id }) { tarea ->
                        ItemTarea(
                            tarea = tarea, 
                            isSelected = uiState.tareaSeleccionada?.id == tarea.id, 
                            onClick = { viewModel.seleccionarTarea(tarea) },
                            onDeleteClick = { viewModel.eliminarTarea(tarea.id) }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            CronometroSeccion(
                tarea = uiState.tareaSeleccionada,
                tiempoRestante = uiState.tiempoRestante,
                duracionSesionActual = uiState.duracionSesionActual,
                isRunning = uiState.isTimerRunning,
                onToggleTimer = { viewModel.toggleTimer() }
            )
        }
    }
}
