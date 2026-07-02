package com.knexus.ergohabit.features.posture.presentation.viewmodel

data class ConfigNutricionUiState(
    val horaDesayuno: String = "",
    val horaComida: String = "",
    val horaCena: String = "",
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val error: String? = null,
    
    // UI State para el BottomSheet
    val showSheet: Boolean = false,
    val comidaEditando: String = "", // "Desayuno", "Comida", "Cena"
    val emojiEditando: String = "",
    val horaTemp: String = ""
)
