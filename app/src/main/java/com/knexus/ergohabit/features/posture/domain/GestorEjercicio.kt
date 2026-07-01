package com.knexus.ergohabit.features.posture.domain

import com.knexus.ergohabit.core.hardware.domain.model.DatosPasos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GestorEjercicio @Inject constructor() {
    private val _datosPasos = MutableStateFlow(DatosPasos(0, 0.0))
    val datosPasos: StateFlow<DatosPasos> = _datosPasos.asStateFlow()

    private val _estaCorriendo = MutableStateFlow(false)
    val estaCorriendo: StateFlow<Boolean> = _estaCorriendo.asStateFlow()

    fun actualizarDatos(pasos: Int, km: Double) {
        _datosPasos.value = DatosPasos(pasos, km)
    }

    fun setCorriendo(corriendo: Boolean) {
        _estaCorriendo.value = corriendo
        if (!corriendo) {
            // No reiniciamos los datos aquí para que el ViewModel pueda sincronizarlos después de parar
        }
    }

    fun reiniciar() {
        _datosPasos.value = DatosPasos(0, 0.0)
    }
}