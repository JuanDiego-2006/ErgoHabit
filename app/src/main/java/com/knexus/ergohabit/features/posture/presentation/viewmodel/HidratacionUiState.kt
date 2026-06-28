package com.knexus.ergohabit.features.posture.presentation.viewmodel

data class HidratacionUiState(
    val mlActuales: Int = 0,
    val mlObjetivo: Int = 2450,
    val vasosObjetivo: Int = 10,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    
    // Diálogos y Modales
    val mostrarDialogoMeta: Boolean = false,
    val mostrarDialogoPesoEstatura: Boolean = false,
    val mostrarDialogoMetaManual: Boolean = false,
    val mostrarDialogoCustomAmount: Boolean = false,
    val metodoCalculoSeleccionado: Int = 0, // 0: Auto (Peso/Estatura), 1: Manual
    
    // Datos de cálculo
    val pesoActual: Int = 70,
    val estaturaActual: Double = 1.70,
    val pesoInput: String = "70",
    val estaturaInput: String = "1.70",
    val metaManualInput: String = "2000",
    val editandoPeso: Boolean = true, // true: Peso, false: Estatura
    val metaManualTemporal: Int = 2000,
    val customAmountTemporal: Int = 250,
    val fraseMotivacional: String = "¡Mantente hidratado hoy!",
    val tipsHidratacion: List<String> = emptyList()
) {
    // --- CAMBIO: Tope de porcentaje al 100% (1.0f) ---
    val porcentaje: Float get() = if (mlObjetivo > 0) (mlActuales / mlObjetivo.toFloat()).coerceAtMost(1f) else 0f
    // -------------------------------------------------
    val mlRestantes: Int get() = (mlObjetivo - mlActuales).coerceAtLeast(0)
    val vasosRestantes: Int get() = (mlRestantes / 245.0).toInt()
    val vasosActuales: Int get() = (mlActuales / 245.0).toInt()
    
    // Fórmula simple de hidratación: peso * 35ml
    val metaRecomendada: Int get() = (pesoActual * 35.0).toInt()
    
    val vasosMetaManual: Int get() = (metaManualTemporal / 250.0).toInt()
    val litrosMetaManual: Float get() = metaManualTemporal / 1000f
    val vasosCustom: Float get() = customAmountTemporal / 250f

    // Función auxiliar para formatear valores en la UI
    fun formatWater(ml: Int): Pair<String, String> {
        return if (ml <= 900) {
            ml.toString() to "ml"
        } else {
            val litros = ml / 1000f
            String.format(java.util.Locale.US, "%.2f", litros).trimEnd('0').trimEnd('.') to "L"
        }
    }
}
