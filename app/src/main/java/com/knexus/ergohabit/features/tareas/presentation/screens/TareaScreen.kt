package com.knexus.ergohabit.features.tareas.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.knexus.ergohabit.features.posture.presentation.components.BarraNavegacionInferior
import com.knexus.ergohabit.features.tareas.domain.entities.TareaEnfoque
import com.knexus.ergohabit.features.tareas.presentation.viewmodel.TareaViewModel
import com.knexus.ergohabit.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareaScreen(
    navController: NavHostController,
    viewModel: TareaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState()

    if (uiState.mostrarRecordatorioEstiramiento) {
        AlertDialog(
            onDismissRequest = { viewModel.descartarRecordatorio() },
            title = { Text("¡Tiempo de estirar! 🧘", fontWeight = FontWeight.Bold) },
            text = { 
                Text("Has estado trabajando por 25 minutos. Tómate un breve respiro para estirar tu espalda y descansar la vista.") 
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.descartarRecordatorio() },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Text("¡Entendido!", color = Color.White)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = BgWhite
        )
    }

    if (uiState.mostrarSheetNuevaTarea) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.mostrarSheetNuevaTarea(false) },
            sheetState = sheetState,
            containerColor = Color.White,
            dragHandle = { BottomSheetDefaults.DragHandle(color = TextGray.copy(alpha = 0.3f)) }
        ) {
            SheetNuevaTarea(
                titulo = uiState.nuevoTitulo,
                onTituloChange = { viewModel.onTituloCambiado(it) },
                categoriaId = uiState.nuevaCategoriaId,
                onCategoriaSelect = { viewModel.onCategoriaSeleccionada(it) },
                duracion = uiState.nuevaDuracion,
                onDuracionClick = { viewModel.mostrarSelectorDuracion(true) },
                onAgregarClick = { viewModel.agregarTarea() },
                onCerrarClick = { viewModel.mostrarSheetNuevaTarea(false) }
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

    Scaffold(
        containerColor = BgMain,
        bottomBar = { BarraNavegacionInferior(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "FOCO ACADÉMICO",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextGray,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Mis Tareas",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(
                    onClick = { viewModel.mostrarSheetNuevaTarea(true) },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(TextPrimary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "PENDIENTES · ${uiState.tareas.size}",
                style = MaterialTheme.typography.labelMedium,
                color = TextGray,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Lista de Tareas
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.tareas) { tarea ->
                    ItemTarea(
                        tarea = tarea,
                        isSelected = uiState.tareaSeleccionada?.id == tarea.id,
                        onClick = { viewModel.seleccionarTarea(tarea) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sección Cronómetro
            CronometroSeccion(
                tarea = uiState.tareaSeleccionada,
                tiempoRestante = uiState.tiempoRestante,
                isRunning = uiState.isTimerRunning,
                onToggleTimer = { viewModel.toggleTimer() }
            )
        }
    }
}

@Composable
fun ItemTarea(
    tarea: TareaEnfoque,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = when (tarea.idCategoria) {
        1 -> MoradoAcento
        2 -> ActividadVerde
        else -> OrangeAccent
    }

    val icon = when (tarea.idCategoria) {
        1 -> "🎓"
        2 -> "🧘"
        else -> "🧠"
    }

    val categoriaNombre = when (tarea.idCategoria) {
        1 -> "Académica"
        2 -> "Bienestar"
        else -> "Enfoque Profundo"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BgWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(borderColor)
            )
            
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tarea.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = icon, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = categoriaNombre,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                }
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextGray.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun SheetNuevaTarea(
    titulo: String,
    onTituloChange: (String) -> Unit,
    categoriaId: Int,
    onCategoriaSelect: (Int) -> Unit,
    duracion: Int,
    onDuracionClick: () -> Unit,
    onAgregarClick: () -> Unit,
    onCerrarClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Nueva Tarea",
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = onCerrarClick,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(BgMain)
            ) {
                Icon(
                    Icons.Outlined.Close,
                    contentDescription = "Cerrar",
                    tint = TextGray,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "TÍTULO *",
            style = MaterialTheme.typography.labelLarge,
            color = TextGray,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = titulo,
            onValueChange = onTituloChange,
            placeholder = { Text("Ej. Reporte de laboratorio", color = TextGray.copy(alpha = 0.5f)) },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = BgMain,
                unfocusedContainerColor = BgMain,
                disabledContainerColor = BgMain,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "CATEGORÍA",
            style = MaterialTheme.typography.labelLarge,
            color = TextGray,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        val categorias = listOf(
            Triple(1, "Académica", "🎓"),
            Triple(2, "Bienestar", "🧘"),
            Triple(3, "Laboral", "💼"),
            Triple(4, "Enfoque Profundo", "🧠"),
            Triple(5, "Personal", "🏠")
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            categorias.forEach { (id, nombre, icono) ->
                ItemCategoriaSeleccionable(
                    nombre = nombre,
                    icono = icono,
                    isSelected = categoriaId == id,
                    onClick = { onCategoriaSelect(id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "DURACIÓN",
            style = MaterialTheme.typography.labelLarge,
            color = TextGray,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(BgMain)
                .clickable { onDuracionClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(TextPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Timer, contentDescription = null, tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "$duracion min",
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextGray)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onAgregarClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GreenPrimary.copy(alpha = if (titulo.isNotBlank()) 1f else 0.5f)
            ),
            shape = RoundedCornerShape(16.dp),
            enabled = titulo.isNotBlank()
        ) {
            Text(
                text = "+ Agregar Tarea",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun ItemCategoriaSeleccionable(
    nombre: String,
    icono: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) MoradoAcento.copy(alpha = 0.1f) else BgMain
    val borderColor = if (isSelected) MoradoAcento else Color.Transparent

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        border = if (isSelected) BorderStroke(1.dp, borderColor) else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icono, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = nombre,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) MoradoAcento else TextPrimary,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = MoradoAcento,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun SelectorDuracionDialog(
    input: String, // Formato HHMM
    onNumeroClick: (String) -> Unit,
    onBorrarClick: () -> Unit,
    onConfirmar: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = TextPrimary // Fondo verde oscuro como en la imagen
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar", color = BgMain.copy(alpha = 0.7f), fontSize = 16.sp)
                    }
                    Text(
                        "DURACIÓN",
                        color = BgMain,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontSize = 14.sp
                    )
                    TextButton(onClick = onConfirmar) {
                        Text("Listo", color = BgMain.copy(alpha = 0.7f), fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.weight(0.5f))

                // Time Display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom
                ) {
                    val horas = input.substring(0, 2)
                    val minutos = input.substring(2, 4)

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = horas,
                            style = MaterialTheme.typography.displayLarge,
                            color = GreenProgress,
                            fontSize = 80.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text("h", color = GreenProgress.copy(alpha = 0.5f), fontSize = 16.sp)
                    }
                    
                    Text(
                        text = ":",
                        style = MaterialTheme.typography.displayLarge,
                        color = GreenProgress,
                        fontSize = 80.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = minutos,
                            style = MaterialTheme.typography.displayLarge,
                            color = GreenProgress,
                            fontSize = 80.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text("min", color = GreenProgress.copy(alpha = 0.5f), fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.weight(0.5f))

                // Keyboard
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    val rows = listOf(
                        listOf("1", "2", "3"),
                        listOf("4", "5", "6"),
                        listOf("7", "8", "9"),
                        listOf("", "0", "backspace")
                    )

                    rows.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            row.forEach { key ->
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clickable(enabled = key.isNotEmpty()) {
                                            if (key == "backspace") onBorrarClick()
                                            else onNumeroClick(key)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (key == "backspace") {
                                        Icon(
                                            Icons.Default.Backspace,
                                            contentDescription = "Borrar",
                                            tint = BgMain,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    } else if (key.isNotEmpty()) {
                                        Text(
                                            text = key,
                                            style = MaterialTheme.typography.headlineMedium,
                                            color = BgMain,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
fun CronometroSeccion(
    tarea: TareaEnfoque?,
    tiempoRestante: Int,
    isRunning: Boolean,
    onToggleTimer: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BgWhite)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (tarea != null) {
                Text(
                    text = if(tarea.idCategoria == 1) "🎓 Académica" else "Tarea de Enfoque",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextGray
                )
                Text(
                    text = tarea.titulo,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = "Selecciona una tarea",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextGray
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Box(contentAlignment = Alignment.Center) {
                val progreso = if (tarea != null && tarea.duracionTarea > 0) {
                    tiempoRestante.toFloat() / (tarea.duracionTarea * 60f)
                } else 1f

                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(200.dp),
                    color = BgMain,
                    strokeWidth = 8.dp,
                    strokeCap = StrokeCap.Round
                )
                CircularProgressIndicator(
                    progress = { progreso },
                    modifier = Modifier.size(200.dp),
                    color = if (tarea?.idCategoria == 1) MoradoAcento else GreenProgress,
                    strokeWidth = 8.dp,
                    strokeCap = StrokeCap.Round
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "LISTO",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextGray,
                        letterSpacing = 2.sp
                    )
                    val minutos = tiempoRestante / 60
                    val segundos = tiempoRestante % 60
                    Text(
                        text = String.format(Locale.getDefault(), "%02d:%02d", minutos, segundos),
                        style = MaterialTheme.typography.displayMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "minutos",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Sesión continua · ¡Mantén el foco!",
                style = MaterialTheme.typography.bodySmall,
                color = TextGray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onToggleTimer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                shape = RoundedCornerShape(28.dp),
                enabled = tarea != null
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isRunning) "Pausar" else "Iniciar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
