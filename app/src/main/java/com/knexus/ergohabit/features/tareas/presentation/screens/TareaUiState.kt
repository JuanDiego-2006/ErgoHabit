package com.knexus.ergohabit.features.tareas.presentation.screens

import com.knexus.ergohabit.features.tareas.domain.entities.CategoriaTarea
import com.knexus.ergohabit.features.tareas.domain.entities.TareaEnfoque

data class TareaUiState(
    val isLoading: Boolean = false,
    val tareas: List<TareaEnfoque> = emptyList(),
    val categorias: List<CategoriaTarea> = emptyList(),
    val tareaSeleccionada: TareaEnfoque? = null,
    val error: String? = null,
    val tiempoRestante: Int = 0, // En segundos para el cronómetro
    val isTimerRunning: Boolean = false,
    val mostrarRecordatorioEstiramiento: Boolean = false,
    
    // Estado para Nueva Tarea
    val mostrarSheetNuevaTarea: Boolean = false,
    val nuevoTitulo: String = "",
    val nuevaCategoriaId: Int = 1,
    val nuevaDuracion: Int = 45, // minutos
    val mostrarSelectorDuracion: Boolean = false,
    val nuevaDuracionInput: String = "0000", // Formato HHMM
    val mostrarSheetCompletado: Boolean = false,
    val mostrarSheetMasTiempo: Boolean = false,
    val tiempoAdicional: Int = 30, // Minutos
    val mostrarMensajeExito: Boolean = false,
    val mensajeExito: String = ""
)
