package com.knexus.ergohabit.features.posture.domain.repository

import com.knexus.ergohabit.features.posture.data.models.SuenoResponse
import kotlinx.coroutines.flow.Flow

interface SuenoRepository {
    fun getSuenoDashboard(): Flow<Result<SuenoResponse>>
    suspend fun registrarDespertar(): Result<String>
    suspend fun configurarHorario(horaDespertar: String, horaDormir: String): Result<String>
    suspend fun setAlertaActiva(activa: Boolean)
    suspend fun silenciarAlerta()
    suspend fun setNotificacionesHabilitadas(habilitadas: Boolean)
}
