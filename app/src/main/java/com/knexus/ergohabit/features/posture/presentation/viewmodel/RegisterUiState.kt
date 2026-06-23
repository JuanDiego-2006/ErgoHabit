package com.knexus.ergohabit.features.posture.presentation.viewmodel

data class RegisterUiState(
    val nombre: String = "",
    val primerApellido: String = "",
    val segundoApellido: String = "",
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegisterSuccess: Boolean = false
)