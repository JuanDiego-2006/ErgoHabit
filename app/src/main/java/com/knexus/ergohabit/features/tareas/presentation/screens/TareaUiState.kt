package com.knexus.ergohabit.features.tareas.presentation.screens

import com.knexus.ergohabit.features.tareas.domain.entities.TareaEnfoque

data class TareaUiState(
    val isLoading: Boolean = false,
    val tareas: List<TareaEnfoque> = emptyList(),
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
    val mostrarSelectorDuracion: Boolean = false
)
