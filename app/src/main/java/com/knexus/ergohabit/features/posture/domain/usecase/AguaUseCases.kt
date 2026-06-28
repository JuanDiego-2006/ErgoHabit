package com.knexus.ergohabit.features.posture.domain.usecase

import com.knexus.ergohabit.features.posture.domain.entities.DashboardAgua
import com.knexus.ergohabit.features.posture.domain.entities.RegistroSemanalAgua
import com.knexus.ergohabit.features.posture.domain.repository.AguaRepository
import javax.inject.Inject

class GetDashboardAguaUseCase @Inject constructor(private val repository: AguaRepository) {
    suspend operator fun invoke(): Result<DashboardAgua> = repository.getDashboardAgua()
}

class RegistrarTomaAguaUseCase @Inject constructor(private val repository: AguaRepository) {
    suspend operator fun invoke(cantidadMl: Int): Result<String> = repository.registrarToma(cantidadMl)
}

class ConfigurarMetaManualAguaUseCase @Inject constructor(private val repository: AguaRepository) {
    suspend operator fun invoke(metaMl: Int): Result<String> = repository.configurarMetaManual(metaMl)
}

class ConfigurarMetaPesoAguaUseCase @Inject constructor(private val repository: AguaRepository) {
    suspend operator fun invoke(peso: Double, estatura: Double): Result<String> = repository.configurarMetaPeso(peso, estatura)
}

class GetProgresoSemanalAguaUseCase @Inject constructor(private val repository: AguaRepository) {
    suspend operator fun invoke(): Result<RegistroSemanalAgua> = repository.getProgresoSemanal()
}
