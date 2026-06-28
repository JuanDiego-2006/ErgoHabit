package com.knexus.ergohabit.features.tareas.presentation.screens

import com.knexus.ergohabit.features.tareas.domain.entities.TareaEnfoque
import com.knexus.ergohabit.features.tareas.domain.entities.TareasEstado

data class TareaUiState(
    val isLoading: Boolean = false,
    val tareasEstado: TareasEstado? = null,
    val error: String? = null,
    val successMessage: String? = null, // Para mostrar en el Snackbar
    
    // Timer
    val tareaSeleccionada: TareaEnfoque? = null,
    val tiempoRestante: Int = 0, // En segundos
    val isTimerRunning: Boolean = false,
    val mostrarRecordatorioEstiramiento: Boolean = false,
    
    // Nueva Tarea
    val mostrarSheetNuevaTarea: Boolean = false,
    val nuevoTitulo: String = "",
    val nuevaCategoriaNombre: String = "Académica",
    val nuevaDuracion: Int = 45, // Minutos
    val mostrarSelectorDuracion: Boolean = false,
    val nuevaDuracionInput: String = "0000",
    
    // Completado
    val mostrarSheetCompletado: Boolean = false,
    val mostrarSheetMasTiempo: Boolean = false,
    val tiempoAdicional: Int = 15,
    val mostrarMensajeExito: Boolean = false,
    val mensajeExito: String = "",
    
    // Alerta Salud (API cronometro)
    val mostrarAlertaSalud: Boolean = false,
    val alertaSaludInfo: com.knexus.ergohabit.features.tareas.domain.entities.AlertaSalud? = null
)
