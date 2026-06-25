package com.knexus.ergohabit.features.posture.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knexus.ergohabit.features.posture.domain.usecase.PosturaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class PosturaViewModel @Inject constructor(
    private val casoUsoPostura: PosturaUseCase
    // ¡ELIMINAMOS EL GESTOR DE SONIDO DE AQUÍ PARA QUE NO VIBRE DOBLE!
) : ViewModel() {

    private val _estadoUi = MutableStateFlow(PosturaUiState())
    val estadoUi: StateFlow<PosturaUiState> = _estadoUi.asStateFlow()

    private var monitoreoJob: Job? = null

    fun alternarMonitoreo() {
        if (_estadoUi.value.estaMonitoreando) detenerMonitoreo()
        else iniciarMonitoreo()
    }

    private fun iniciarMonitoreo() {
        _estadoUi.update { it.copy(estaMonitoreando = true, conteoVibraciones = 0) }

        monitoreoJob = viewModelScope.launch {
            var vibrandoAnteriormente = false

            casoUsoPostura().collect { entidad ->
                val esIncorrectaAhora = !entidad.esCorrecta

                // Solo sumamos al contador visual de la pantalla
                if (esIncorrectaAhora && !vibrandoAnteriormente) {
                    _estadoUi.update { it.copy(conteoVibraciones = it.conteoVibraciones + 1) }
                }
                vibrandoAnteriormente = esIncorrectaAhora

                // Transformamos los datos absolutos para la UI
                val gradosMalos = abs(90.0 - abs(entidad.anguloPitch)).toInt()
                val gradosAbsolutos = gradosMalos.coerceIn(0, 60)

                _estadoUi.update {
                    it.copy(
                        anguloPitch     = entidad.anguloPitch,
                        anguloRoll      = entidad.anguloRoll,
                        esCorrecta      = entidad.esCorrecta,
                        mensaje         = entidad.mensaje,
                        gradosDisplay   = gradosAbsolutos
                    )
                }

                // ¡AQUÍ ESTABA EL ERROR! El ViewModel ya no mandará a vibrar nada.
                // Dejamos que el PostureForegroundService se encargue del sonido de forma inteligente.
            }
        }
    }

    private fun detenerMonitoreo() {
        monitoreoJob?.cancel()
        monitoreoJob = null
        _estadoUi.update { it.copy(estaMonitoreando = false, mensaje = "Inactivo", gradosDisplay = 0) }
    }
}