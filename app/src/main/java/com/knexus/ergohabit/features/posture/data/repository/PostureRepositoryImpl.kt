package com.knexus.ergohabit.features.posture.data.repository

import android.util.Log
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
    private val posturaApi: PosturaApi
) : PostureRepository {

    override fun getPostureData(): Flow<EntidadPostura> {
        return sensorPostura.iniciarMonitoreo().map { datosSensor ->
            mapeador.mapearAEntidad(datosSensor)
        }
    }

    override suspend fun enviarReportePostura(entidad: EntidadPostura): Result<Boolean> {
        return try {
            val totalAlertas = if (entidad.esCorrecta) 0 else 1
            posturaApi.sincronizarAlertas(RegistroPosturaRequest(totalAlertas = totalAlertas))
            Log.d("RepositorioPostura", "Alertas sincronizadas con el servidor")
            Result.success(true)
        } catch (e: Exception) {
            Log.e("RepositorioPostura", "Error al sincronizar postura", e)
            Result.failure(e)
        }
    }
}
