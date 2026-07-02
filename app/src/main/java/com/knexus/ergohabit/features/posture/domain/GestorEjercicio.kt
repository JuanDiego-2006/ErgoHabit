package com.knexus.ergohabit.features.posture.domain

import com.knexus.ergohabit.core.hardware.domain.model.DatosPasos
import com.knexus.ergohabit.features.posture.domain.repository.EjercicioRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GestorEjercicio @Inject constructor(
    private val repository: EjercicioRepository
) {
    private val _datosPasos = MutableStateFlow(DatosPasos(0, 0.0))
    val datosPasos: StateFlow<DatosPasos> = _datosPasos.asStateFlow()

    private val _estaCorriendo = MutableStateFlow(false)
    val estaCorriendo: StateFlow<Boolean> = _estaCorriendo.asStateFlow()

    private var pasosBase = 0
    private var kmBase = 0.0
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        // Al crear el Singleton (inicio de app), cargamos lo que haya en Room
        scope.launch {
            val guardados = repository.obtenerPasosTemporales()
            pasosBase = guardados.first
            kmBase = guardados.second
            _datosPasos.value = DatosPasos(pasosBase, kmBase)
        }
    }

    fun actualizarDatos(pasosSesion: Int, kmSesion: Double) {
        val totalPasos = pasosBase + pasosSesion
        val totalKm = kmBase + kmSesion
        
        _datosPasos.value = DatosPasos(totalPasos, totalKm)
        
        // Guardamos en Room para que persista si la app se cierra
        scope.launch {
            repository.guardarPasosTemporales(totalPasos, totalKm)
        }
    }

    fun setCorriendo(corriendo: Boolean) {
        if (!corriendo && _estaCorriendo.value) {
            pasosBase = _datosPasos.value.pasos
            kmBase = _datosPasos.value.km
            // Aseguramos persistencia al detener
            scope.launch {
                repository.guardarPasosTemporales(pasosBase, kmBase)
            }
        }
        _estaCorriendo.value = corriendo
    }

    fun reiniciar() {
        pasosBase = 0
        kmBase = 0.0
        _datosPasos.value = DatosPasos(0, 0.0)
        scope.launch {
            repository.limpiarPasosTemporales()
        }
    }
}
