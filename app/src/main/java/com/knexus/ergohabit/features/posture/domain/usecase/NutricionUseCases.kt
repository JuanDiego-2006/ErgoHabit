package com.knexus.ergohabit.features.posture.domain.usecase

import com.knexus.ergohabit.features.posture.domain.repository.NutricionRepository
import javax.inject.Inject

class GetNutricionDashboardUseCase @Inject constructor(
    private val repository: NutricionRepository
) {
    operator fun invoke() = repository.getNutricionDashboard()
}

class ConfigurarHorariosNutricionUseCase @Inject constructor(
    private val repository: NutricionRepository
) {
    suspend operator fun invoke(desayuno: String, comida: String, cena: String) =
        repository.configurarHorarios(desayuno, comida, cena)
}

class MarcarComidaUseCase @Inject constructor(
    private val repository: NutricionRepository
) {
    suspend operator fun invoke(tipo: String, estado: Boolean) =
        repository.marcarComida(tipo, estado)
}

class SetNotificacionesNutricionUseCase @Inject constructor(
    private val repository: NutricionRepository
) {
    suspend operator fun invoke(habilitadas: Boolean) =
        repository.setNotificacionesHabilitadas(habilitadas)
}
