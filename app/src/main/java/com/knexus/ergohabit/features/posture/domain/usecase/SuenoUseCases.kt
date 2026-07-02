package com.knexus.ergohabit.features.posture.domain.usecase

import com.knexus.ergohabit.features.posture.domain.repository.SuenoRepository
import javax.inject.Inject

class GetSuenoDashboardUseCase @Inject constructor(
    private val repository: SuenoRepository
) {
    operator fun invoke() = repository.getSuenoDashboard()
}

class RegistrarDespertarUseCase @Inject constructor(
    private val repository: SuenoRepository
) {
    suspend operator fun invoke() = repository.registrarDespertar()
}

class ConfigurarHorarioSuenoUseCase @Inject constructor(
    private val repository: SuenoRepository
) {
    suspend operator fun invoke(horaDespertar: String, horaDormir: String) =
        repository.configurarHorario(horaDespertar, horaDormir)
}

class SetAlertaSuenoActivaUseCase @Inject constructor(
    private val repository: SuenoRepository
) {
    suspend operator fun invoke(activa: Boolean) =
        repository.setAlertaActiva(activa)
}

class SetNotificacionesSuenoUseCase @Inject constructor(
    private val repository: SuenoRepository
) {
    suspend operator fun invoke(habilitadas: Boolean) =
        repository.setNotificacionesHabilitadas(habilitadas)
}
