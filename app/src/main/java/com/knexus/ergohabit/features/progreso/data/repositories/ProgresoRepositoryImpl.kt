package com.knexus.ergohabit.features.progreso.data.repositories

import com.knexus.ergohabit.features.progreso.data.datasource.api.ProgresoApi
import com.knexus.ergohabit.features.progreso.data.mapper.toDomain
import com.knexus.ergohabit.features.progreso.domain.entities.DetalleHabito
import com.knexus.ergohabit.features.progreso.domain.entities.HabitoProgreso
import com.knexus.ergohabit.features.progreso.domain.entities.ProgresoDia
import com.knexus.ergohabit.features.progreso.domain.entities.RegistroHabito
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
            // Lógica: Semana actual (Lunes a Domingo) para mantener el orden solicitado
            val diasSemana = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
            val calendar = java.util.Calendar.getInstance()
            
            // Obtener el índice del día actual (Lunes = 0, ..., Domingo = 6)
            val dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
            val todayIndex = when (dayOfWeek) {
                java.util.Calendar.MONDAY -> 0
                java.util.Calendar.TUESDAY -> 1
                java.util.Calendar.WEDNESDAY -> 2
                java.util.Calendar.THURSDAY -> 3
                java.util.Calendar.FRIDAY -> 4
                java.util.Calendar.SATURDAY -> 5
                java.util.Calendar.SUNDAY -> 6
                else -> 0
            }

            val registrosDinamicos = (0..6).map { i ->
                val nombreDia = if (i == todayIndex) "Hoy" else diasSemana[i]
                
                // Valores de prueba realistas
                val valor = when(idHabito) {
                    1 -> (60..95).random() / 10f // Sueño: 6.0 a 9.5h
                    2 -> (12..25).random() / 10f // Hidratación: 1.2 a 2.5L
                    3 -> (20..110).random() / 10f // Ejercicio: 2.0 a 11.0km
                    4 -> (1..10).random().toFloat() // Postura: 1 a 10 alertas
                    else -> (15..50).random().toFloat()
                }
                val meta = when(idHabito) {
                    1 -> 8f
                    2 -> 2f
                    3 -> 8f 
                    4 -> 0f // Para alertas, 0 es lo ideal
                    else -> 30f
                }
                // Para postura, cualquier alerta se marca en rojo (o invertimos la lógica)
                val cumplida = if(idHabito == 4) false else valor >= meta
                RegistroHabito(nombreDia, valor, cumplida)
            }

            val mockDetalle = when(idHabito) {
                1 -> DetalleHabito(
                    idHabito = 1,
                    titulo = "HORAS DE SUEÑO",
                    metaValor = 8f,
                    registros = registrosDinamicos,
                    leyendaPositiva = "Verde = meta cumplida",
                    leyendaNegativa = "Rojo = meta no cumplida"
                )
                2 -> DetalleHabito(
                    idHabito = 2,
                    titulo = "HIDRATACIÓN (L)",
                    metaValor = 2f,
                    registros = registrosDinamicos,
                    leyendaPositiva = "Verde = meta cumplida",
                    leyendaNegativa = "Rojo = meta no cumplida"
                )
                3 -> DetalleHabito(
                    idHabito = 3,
                    titulo = "DISTANCIA RECORRIDA (KM)",
                    metaValor = 8f,
                    registros = registrosDinamicos,
                    leyendaPositiva = "Verde = meta cumplida",
                    leyendaNegativa = "Rojo = meta no cumplida"
                )
                4 -> DetalleHabito(
                    idHabito = 4,
                    titulo = "ALERTAS DE POSTURA",
                    metaValor = 0f,
                    registros = registrosDinamicos,
                    leyendaPositiva = "", 
                    leyendaNegativa = "Menos alertas ⚠️ = mejor postura durante la semana 📉"
                )
                else -> DetalleHabito(
                    idHabito = idHabito,
                    titulo = "ACTIVIDAD",
                    metaValor = 30f,
                    registros = registrosDinamicos,
                    leyendaPositiva = "Verde = meta cumplida",
                    leyendaNegativa = "Rojo = meta no cumplida"
                )
            }
            emit(Result.success(mockDetalle))
        }
    }
}
