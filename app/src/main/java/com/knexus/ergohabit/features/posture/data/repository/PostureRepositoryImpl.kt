package com.knexus.ergohabit.features.posture.data.repository

import android.util.Log
import com.knexus.ergohabit.core.database.dao.PosturaDao
import com.knexus.ergohabit.core.database.entities.PosturaEntity
import com.knexus.ergohabit.core.hardware.domain.SensorPostura
import com.knexus.ergohabit.features.posture.data.datasource.api.PosturaApi
import com.knexus.ergohabit.features.posture.data.datasource.api.RegistroPosturaRequest
import com.knexus.ergohabit.features.posture.data.mapper.PostureMapper
import com.knexus.ergohabit.features.posture.domain.entities.EntidadPostura
import com.knexus.ergohabit.features.posture.domain.repository.PostureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PostureRepositoryImpl @Inject constructor(
    private val sensorPostura: SensorPostura,
    private val mapeador: PostureMapper,
    private val posturaApi: PosturaApi,
    private val posturaDao: PosturaDao
) : PostureRepository {

    override fun getPostureData(): Flow<EntidadPostura> {
        return sensorPostura.iniciarMonitoreo().map { datosSensor ->
            mapeador.mapearAEntidad(datosSensor)
        }
    }

    override suspend fun guardarReporteLocal(entidad: EntidadPostura): Result<Unit> {
        return try {
            if (!entidad.esCorrecta) {
                posturaDao.insertarAlerta(PosturaEntity())
                Log.d("RepositorioPostura", "Alerta guardada localmente")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("RepositorioPostura", "Error al guardar alerta localmente", e)
            Result.failure(e)
        }
    }

    override suspend fun sincronizarConServidor(): Result<Boolean> {
        return try {
            val totalAlertas = posturaDao.obtenerConteoNoSincronizados()
            Log.d("RepositorioPostura", "Iniciando ciclo de sincronización. Alertas pendientes: $totalAlertas")
            if (totalAlertas > 0) {
                posturaApi.sincronizarAlertas(RegistroPosturaRequest(totalAlertas = totalAlertas))
                posturaDao.marcarComoSincronizados()
                posturaDao.eliminarSincronizados()
                Log.d("RepositorioPostura", "Sincronización EXITOSA: Se enviaron $totalAlertas alertas")
            } else {
                Log.d("RepositorioPostura", "Sincronización OMITIDA: No hay alertas nuevas en Room")
            }
            Result.success(true)
        } catch (e: Exception) {
            Log.e("RepositorioPostura", "ERROR de sincronización: ${e.message}", e)
            Result.failure(e)
        }
    }
}
