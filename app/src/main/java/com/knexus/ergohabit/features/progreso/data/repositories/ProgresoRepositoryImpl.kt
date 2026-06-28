package com.knexus.ergohabit.features.progreso.data.repositories

import com.knexus.ergohabit.core.session.SessionManager
import com.knexus.ergohabit.features.posture.data.datasource.api.HabitosApi
import com.knexus.ergohabit.features.posture.data.datasource.api.PosturaApi
import com.knexus.ergohabit.features.posture.data.models.HistorialHabitoResponse
import com.knexus.ergohabit.features.posture.data.models.ProgresoPosturaResponseDto
import com.knexus.ergohabit.features.progreso.data.datasource.api.ProgresoDiarioApi
import com.knexus.ergohabit.features.progreso.data.models.ProgresoDiarioDto
import com.knexus.ergohabit.features.progreso.domain.entities.DetalleHabito
import com.knexus.ergohabit.features.progreso.domain.entities.HabitoProgreso
import com.knexus.ergohabit.features.progreso.domain.entities.ProgresoDia
import com.knexus.ergohabit.features.progreso.domain.entities.RegistroHabito
import com.knexus.ergohabit.features.progreso.domain.repositories.ProgresoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.math.roundToInt

class ProgresoRepositoryImpl @Inject constructor(
    private val progresoDiarioApi: ProgresoDiarioApi,
    private val habitosApi: HabitosApi,
    private val posturaApi: PosturaApi,
    private val sessionManager: SessionManager
) : ProgresoRepository {

    override fun getHabitosProgreso(idUsuario: Int): Flow<Result<List<HabitoProgreso>>> = flow {
        try {
            val progreso = progresoDiarioApi.getProgresoDiario(resolveUserId(idUsuario))
            emit(Result.success(progreso.toHabitosProgreso()))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getTendenciaGeneral(idUsuario: Int): Flow<Result<Pair<String, List<ProgresoDia>>>> = flow {
        try {
            val progreso = progresoDiarioApi.getProgresoDiario(resolveUserId(idUsuario))
            val promedio = listOf(
                progreso.porcentajeSueno,
                progreso.porcentajeAgua,
                progreso.porcentajeEjercicio,
                progreso.porcentajeNutricion
            ).average()

            val historialAgua = habitosApi.obtenerProgresoAgua()
            val tendencia = historialAgua.datosGrafica.mapIndexed { index, item ->
                ProgresoDia(
                    dia = index + 1,
                    valor = (item.valor / 3.0).toFloat().coerceIn(0f, 1f)
                )
            }

            emit(Result.success("${promedio.roundToInt()}%" to tendencia))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getDetalleHabito(idUsuario: Int, idHabito: Int): Flow<Result<DetalleHabito>> = flow {
        try {
            resolveUserId(idUsuario)
            val detalle = when (idHabito) {
                1 -> habitosApi.obtenerProgresoSueno().toDetalleHabito(1, 8f)
                2 -> habitosApi.obtenerProgresoAgua().toDetalleHabito(2, 2f)
                3 -> habitosApi.obtenerProgresoEjercicio().toDetalleHabito(3, 8f)
                4 -> posturaApi.obtenerProgresoSemanal().toDetallePostura()
                else -> throw IllegalArgumentException("Hábito no soportado")
            }
            emit(Result.success(detalle))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    private fun resolveUserId(fallback: Int): Int {
        return sessionManager.fetchUserId() ?: fallback
    }

    private fun ProgresoDiarioDto.toHabitosProgreso(): List<HabitoProgreso> = listOf(
        HabitoProgreso(1, "Sueño", "🌙", "#7C6FF7", porcentajeSueno.toInt().coerceIn(0, 100)),
        HabitoProgreso(2, "Agua", "💧", "#29B6F6", porcentajeAgua.toInt().coerceIn(0, 100)),
        HabitoProgreso(3, "Ejercicio", "🏃", "#34C97A", porcentajeEjercicio.toInt().coerceIn(0, 100)),
        HabitoProgreso(4, "Postura", "🧘", "#2E7D52", posturaPct(totalAlertasPostura))
    )

    private fun posturaPct(alertas: Int): Int = when {
        alertas == 0 -> 100
        alertas <= 3 -> 75
        alertas <= 7 -> 50
        else -> 25
    }

    private fun HistorialHabitoResponse.toDetalleHabito(idHabito: Int, metaValor: Float): DetalleHabito {
        return DetalleHabito(
            idHabito = idHabito,
            titulo = tituloSeccion,
            metaValor = metaValor,
            leyendaPositiva = "Verde = meta cumplida",
            leyendaNegativa = "Rojo = meta no cumplida",
            registros = datosGrafica.map {
                RegistroHabito(
                    etiqueta = it.diaSemana,
                    valor = it.valor.toFloat(),
                    esMetaCumplida = it.metaCumplida
                )
            }
        )
    }

    private fun ProgresoPosturaResponseDto.toDetallePostura(): DetalleHabito {
        return DetalleHabito(
            idHabito = 4,
            titulo = "ALERTAS DE POSTURA",
            metaValor = 0f,
            leyendaPositiva = "",
            leyendaNegativa = mensajeMeta.ifBlank { "Menos alertas = mejor postura" },
            registros = datosGrafica.map {
                RegistroHabito(
                    etiqueta = it.diaSemana,
                    valor = it.totalAlertas.toFloat(),
                    esMetaCumplida = it.totalAlertas == 0
                )
            }
        )
    }
}
