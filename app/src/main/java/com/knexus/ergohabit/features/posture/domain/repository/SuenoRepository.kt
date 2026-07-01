package com.knexus.ergohabit.features.posture.domain.repository

import com.knexus.ergohabit.features.posture.data.models.SuenoResponse
import kotlinx.coroutines.flow.Flow

interface SuenoRepository {
    fun getSuenoDashboard(): Flow<Result<SuenoResponse>>
    suspend fun registrarDespertar(): Result<String>
    suspend fun configurarHorario(horaDespertar: String, horaDormir: String): Result<String>
}
