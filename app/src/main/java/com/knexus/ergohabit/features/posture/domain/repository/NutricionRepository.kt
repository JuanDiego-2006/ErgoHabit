package com.knexus.ergohabit.features.posture.domain.repository

import com.knexus.ergohabit.features.posture.data.models.NutricionDashboardResponse
import kotlinx.coroutines.flow.Flow

interface NutricionRepository {
    fun getNutricionDashboard(): Flow<Result<NutricionDashboardResponse>>
    suspend fun configurarHorarios(desayuno: String, comida: String, cena: String): Result<String>
    suspend fun marcarComida(tipo: String, estado: Boolean): Result<String>
    suspend fun setNotificacionesHabilitadas(habilitadas: Boolean)
}
