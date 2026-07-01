package com.knexus.ergohabit.features.posture.presentation.viewmodel

data class ConfigNutricionUiState(
    val horaDesayuno: String = "08:00",
    val horaComida: String = "14:00",
    val horaCena: String = "20:00",
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val error: String? = null,
    
    // UI State para el BottomSheet
    val showSheet: Boolean = false,
    val comidaEditando: String = "", // "Desayuno", "Comida", "Cena"
    val emojiEditando: String = "",
    val horaTemp: String = ""
)
