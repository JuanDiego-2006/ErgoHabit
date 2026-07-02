package com.knexus.ergohabit.features.posture.data.repository

import com.knexus.ergohabit.core.database.dao.EjercicioDao
import com.knexus.ergohabit.core.database.entities.EjercicioSesionEntity
import com.knexus.ergohabit.features.posture.data.datasource.api.HabitosApi
import com.knexus.ergohabit.features.posture.data.mapper.toDomain
import com.knexus.ergohabit.features.posture.data.mapper.toEntity
import com.knexus.ergohabit.features.posture.data.models.HabitosMetaEjercicioRequest
import com.knexus.ergohabit.features.posture.data.models.RegistrarKmRequest
import com.knexus.ergohabit.features.posture.domain.entities.DashboardEjercicio
import com.knexus.ergohabit.features.posture.domain.repository.EjercicioRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class EjercicioRepositoryImpl @Inject constructor(
    private val api: HabitosApi,
    private val dao: EjercicioDao
) : EjercicioRepository {

    override fun getDashboardEjercicio(): Flow<Result<DashboardEjercicio>> = channelFlow {
        // 1. Emitir lo que haya en Room
        launch {
            dao.getDashboard().collect { local ->
                if (local != null) {
                    send(Result.success(local.toDomain()))
                }
            }
        }

        // 2. Intentar actualizar desde la API
        try {
            val response = api.obtenerDashboardEjercicio()
            dao.insertDashboard(response.toEntity())
        } catch (e: Exception) {
            val current = dao.getDashboard().first()
            if (current == null) {
                send(Result.failure(e))
            }
        }
    }

    override suspend fun registrarKilometros(km: Double): Result<String> {
        return try {
            val response = api.registrarKilometros(RegistrarKmRequest(km = km))
            
            // Intentamos actualizar el dashboard local, pero no fallamos si esto falla
            try {
                val freshResponse = api.obtenerDashboardEjercicio()
                dao.insertDashboard(freshResponse.toEntity())
            } catch (e: Exception) {
                android.util.Log.e("EjercicioRepo", "Error al refrescar dashboard: ${e.message}")
            }
            
            Result.success(response.mensaje)
        } catch (e: Exception) {
            android.util.Log.e("EjercicioRepo", "Error fatal al registrar km: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun configurarMeta(nuevaMeta: Double): Result<String> {
        return try {
            val response = api.configurarMetaEjercicio(HabitosMetaEjercicioRequest(nuevaMeta = nuevaMeta))
            
            // Actualización manual inmediata (Igual que en sueño)
            val currentLocal = dao.getDashboard().first()
            if (currentLocal != null) {
                dao.insertDashboard(currentLocal.copy(
                    metaKmText = "de $nuevaMeta km"
                ))
            }

            // Refresco completo desde el servidor
            try {
                val freshResponse = api.obtenerDashboardEjercicio()
                dao.insertDashboard(freshResponse.toEntity())
            } catch (_: Exception) { }

            Result.success(response.mensaje)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun guardarPasosTemporales(pasos: Int, km: Double) {
        dao.saveSesionActual(EjercicioSesionEntity(pasosAcumulados = pasos, kmAcumulados = km))
    }

    override suspend fun obtenerPasosTemporales(): Pair<Int, Double> {
        val entity = dao.getSesionActual()
        return if (entity != null) {
            Pair(entity.pasosAcumulados, entity.kmAcumulados)
        } else {
            Pair(0, 0.0)
        }
    }

    override suspend fun limpiarPasosTemporales() {
        dao.clearSesionActual()
    }
}
