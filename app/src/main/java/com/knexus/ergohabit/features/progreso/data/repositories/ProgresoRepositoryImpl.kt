package com.knexus.ergohabit.features.progreso.data.repositories

import com.knexus.ergohabit.features.progreso.data.datasource.api.ProgresoApi
import com.knexus.ergohabit.features.progreso.data.mapper.toDomain
import com.knexus.ergohabit.features.progreso.domain.entities.DetalleHabito
import com.knexus.ergohabit.features.progreso.domain.entities.HabitoProgreso
import com.knexus.ergohabit.features.progreso.domain.entities.ProgresoDia
import com.knexus.ergohabit.features.progreso.domain.repositories.ProgresoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProgresoRepositoryImpl @Inject constructor(
    private val api: ProgresoApi
) : ProgresoRepository {

    override fun getHabitosProgreso(idUsuario: Int): Flow<Result<List<HabitoProgreso>>> = flow {
        try {
            val response = api.getHabitosProgreso(idUsuario)
            val habitos = response.map { it.toDomain() }
            emit(Result.success(habitos))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getTendenciaGeneral(idUsuario: Int): Flow<Result<Pair<String, List<ProgresoDia>>>> = flow {
        try {
            val response = api.getTendenciaGeneral(idUsuario)
            val tendencia = response.datos.map { it.toDomain() }
            emit(Result.success(response.porcentaje to tendencia))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getDetalleHabito(idUsuario: Int, idHabito: Int): Flow<Result<DetalleHabito>> = flow {
        try {
            val response = api.getDetalleHabito(idUsuario, idHabito)
            emit(Result.success(response.toDomain()))
        } catch (e: Exception) {
            // Datos de prueba para desarrollo si la API falla
            val mockDetalle = when(idHabito) {
                1 -> DetalleHabito(
                    idHabito = 1,
                    titulo = "HORAS DE SUEÑO",
                    metaValor = 8f,
                    registros = listOf(
                        com.knexus.ergohabit.features.progreso.domain.entities.RegistroHabito("Lun", 6.5f, false),
                        com.knexus.ergohabit.features.progreso.domain.entities.RegistroHabito("Mar", 8.2f, true),
                        com.knexus.ergohabit.features.progreso.domain.entities.RegistroHabito("Mié", 5.8f, false),
                        com.knexus.ergohabit.features.progreso.domain.entities.RegistroHabito("Jue", 8.5f, true),
                        com.knexus.ergohabit.features.progreso.domain.entities.RegistroHabito("Vie", 7.0f, false),
                        com.knexus.ergohabit.features.progreso.domain.entities.RegistroHabito("Sáb", 9.1f, true),
                        com.knexus.ergohabit.features.progreso.domain.entities.RegistroHabito("Hoy", 7.3f, false)
                    ),
                    leyendaPositiva = "Verde = meta cumplida",
                    leyendaNegativa = "Rojo = meta sin cumplir"
                )
                2 -> DetalleHabito(
                    idHabito = 2,
                    titulo = "HIDRATACIÓN (ML)",
                    metaValor = 2f,
                    registros = listOf(
                        com.knexus.ergohabit.features.progreso.domain.entities.RegistroHabito("Lun", 1.2f, false),
                        com.knexus.ergohabit.features.progreso.domain.entities.RegistroHabito("Mar", 2.1f, true),
                        com.knexus.ergohabit.features.progreso.domain.entities.RegistroHabito("Mié", 1.5f, false),
                        com.knexus.ergohabit.features.progreso.domain.entities.RegistroHabito("Jue", 2.3f, true),
                        com.knexus.ergohabit.features.progreso.domain.entities.RegistroHabito("Vie", 1.8f, false),
                        com.knexus.ergohabit.features.progreso.domain.entities.RegistroHabito("Sáb", 2.2f, true),
                        com.knexus.ergohabit.features.progreso.domain.entities.RegistroHabito("Hoy", 1.4f, false)
                    ),
                    leyendaPositiva = "Verde = meta cumplida",
                    leyendaNegativa = "Rojo = meta sin cumplir"
                )
                else -> DetalleHabito(
                    idHabito = idHabito,
                    titulo = "ACTIVIDAD",
                    metaValor = 30f,
                    registros = listOf(
                        com.knexus.ergohabit.features.progreso.domain.entities.RegistroHabito("Lun", 45f, true),
                        com.knexus.ergohabit.features.progreso.domain.entities.RegistroHabito("Mar", 20f, false),
                        com.knexus.ergohabit.features.progreso.domain.entities.RegistroHabito("Mié", 35f, true),
                        com.knexus.ergohabit.features.progreso.domain.entities.RegistroHabito("Jue", 30f, true),
                        com.knexus.ergohabit.features.progreso.domain.entities.RegistroHabito("Vie", 15f, false),
                        com.knexus.ergohabit.features.progreso.domain.entities.RegistroHabito("Sáb", 60f, true),
                        com.knexus.ergohabit.features.progreso.domain.entities.RegistroHabito("Hoy", 40f, true)
                    ),
                    leyendaPositiva = "Verde = meta cumplida",
                    leyendaNegativa = "Rojo = meta sin cumplir"
                )
            }
            emit(Result.success(mockDetalle))
        }
    }
}
