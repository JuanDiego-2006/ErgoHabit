package com.knexus.ergohabit.features.posture.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knexus.ergohabit.core.hardware.domain.GestorSonido
import com.knexus.ergohabit.features.posture.domain.usecase.PosturaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PosturaViewModel @Inject constructor(
    private val casoUsoPostura: PosturaUseCase,
    private val gestorSonido: GestorSonido
) : ViewModel() {

    private val _estadoUi = MutableStateFlow(PosturaUiState())
    val estadoUi: StateFlow<PosturaUiState> = _estadoUi.asStateFlow()

    private var vibrandoAnteriormente = false

    fun alternarMonitoreo() {
        if (_estadoUi.value.estaMonitoreando) {
            detenerMonitoreo()
        } else {
            iniciarMonitoreo()
        }
    }

    private fun iniciarMonitoreo() {
        _estadoUi.update { it.copy(estaMonitoreando = true, conteoVibraciones = 0) }
        viewModelScope.launch {
            casoUsoPostura().collect { entidad ->
                val esIncorrectaAhora = !entidad.esCorrecta
                if (esIncorrectaAhora && !vibrandoAnteriormente) {
                    _estadoUi.update { it.copy(conteoVibraciones = it.conteoVibraciones + 1) }
                }
                vibrandoAnteriormente = esIncorrectaAhora
                _estadoUi.update {
                    it.copy(
                        anguloPitch = entidad.anguloPitch,
                        anguloRoll = entidad.anguloRoll,
                        esCorrecta = entidad.esCorrecta,
                        mensaje = entidad.mensaje
                    )
                }
                if (!entidad.esCorrecta) {
                    gestorSonido.sonarAlerta()
                } else {
                    gestorSonido.detenerSonido()
                }
            }
        }
    }

    private fun detenerMonitoreo() {
        _estadoUi.update { it.copy(estaMonitoreando = false, mensaje = "Inactivo") }
        gestorSonido.detenerSonido()
    }
}