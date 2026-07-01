package com.knexus.ergohabit.features.progreso.presentation.screens

import com.knexus.ergohabit.features.progreso.domain.entities.DetalleHabito
import com.knexus.ergohabit.features.progreso.domain.entities.HabitoProgreso
import com.knexus.ergohabit.features.progreso.domain.entities.ProgresoDia

data class ProgresoUiState(
    val isLoading: Boolean = false,
    val habitos: List<HabitoProgreso> = emptyList(),
    val frase: com.knexus.ergohabit.features.progreso.domain.entities.Frase? = null,
    val detalleHabito: DetalleHabito? = null,
    val idHabitoSeleccionado: Int? = null,
    val error: String? = null
)
