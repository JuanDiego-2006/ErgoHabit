package com.knexus.ergohabit.features.posture.data.repository

import android.util.Log
import com.knexus.ergohabit.core.hardware.domain.SensorPostura
import com.knexus.ergohabit.features.posture.data.mapper.PostureMapper
import com.knexus.ergohabit.features.posture.domain.entities.EntidadPostura
import com.knexus.ergohabit.features.posture.domain.repository.PostureRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementación del repositorio de postura siguiendo la arquitectura limpia.
 */
class PostureRepositoryImpl @Inject constructor(
    private val sensorPostura: SensorPostura,
    private val mapeador: PostureMapper
) : PostureRepository {
    
    /**
     * Obtiene el flujo de datos de postura desde el sensor de hardware.
     */
    override fun getPostureData(): Flow<EntidadPostura> {
        return sensorPostura.iniciarMonitoreo().map { datosSensor ->
            mapeador.mapearAEntidad(datosSensor)
        }
    }

    /**
     * Simulación del envío de datos al servidor.
     */
    override suspend fun enviarReportePostura(entidad: EntidadPostura): Result<Boolean> {
        return try {
            val dto = mapeador.mapearADto(entidad)
            Log.d("RepositorioPostura", "Simulando envío al servidor: $dto")
            
            // Simular retraso de red
            delay(1000)
            
            Log.d("RepositorioPostura", "¡Envío simulado con éxito!")
            Result.success(true)
        } catch (e: Exception) {
            Log.e("RepositorioPostura", "Error en el envío simulado", e)
            Result.failure(e)
        }
    }
}
