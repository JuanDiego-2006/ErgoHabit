package com.knexus.ergohabit.features.posture.presentation.viewmodel

data class ConfigMetaUiState(
    val metaSeleccionada: Float = 8.0f,
    val metaActual: Float = 8.0f
) {
    val pasosAproximados: Int get() = (metaSeleccionada * 1250).toInt()
}