package com.knexus.ergohabit.features.progreso.data.repositories

import com.knexus.ergohabit.features.progreso.data.datasource.api.ProgresoApi
import com.knexus.ergohabit.features.progreso.data.mapper.toDomain
import com.knexus.ergohabit.features.progreso.data.models.DatoGraficaDto
import com.knexus.ergohabit.features.progreso.domain.entities.DetalleHabito
import com.knexus.ergohabit.features.progreso.domain.entities.HabitoProgreso
import com.knexus.ergohabit.features.progreso.domain.entities.ProgresoDia
import com.knexus.ergohabit.features.progreso.domain.entities.RegistroHabito
import com.knexus.ergohabit.features.progreso.domain.repositories.ProgresoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Calendar
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


    private fun ordenarSemanaNatural(
        datos: List<DatoGraficaDto>, 
        idHabito: Int
    ): List<RegistroHabito> {
        val diasOrden = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
        
        val datoHoy = datos.find { it.esHoy }
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val indexHoyReal = when (dayOfWeek) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6
            else -> 0
        }

        val datosMap = datos.filter { it.diaSemana != "Hoy" }.associateBy { it.diaSemana }

        return diasOrden.mapIndexed { index, nombreDia ->
            val esHoyIteracion = index == indexHoyReal
            val datoDto = if (esHoyIteracion) datoHoy else datosMap[nombreDia]
            
            val valorReal = if (idHabito == 4) datoDto?.totalAlertas?.toFloat() ?: 0f else datoDto?.valor ?: 0f
            

            val cumplida = if (idHabito == 4) {
                datoDto?.totalAlertas == 0 && (index <= indexHoyReal)
            } else {
                datoDto?.metaCumplida ?: false
            }

            RegistroHabito(
                etiqueta = if (esHoyIteracion) "Hoy" else nombreDia,
                valor = valorReal,
                esMetaCumplida = cumplida
            )
        }
    }

    override fun getDetalleHabito(idUsuario: Int, idHabito: Int): Flow<Result<DetalleHabito>> = flow {
        try {
            when (idHabito) {
                1 -> {
                    val response = api.getProgresoSemanalSueno()
                    emit(Result.success(DetalleHabito(
                        idHabito = 1,
                        titulo = response.tituloSeccion ?: "HORAS DE SUEÑO",
                        metaValor = 8f, 
                        registros = ordenarSemanaNatural(response.datosGrafica, 1),
                        leyendaPositiva = "Verde = meta cumplida",
                        leyendaNegativa = "Rojo = meta no cumplida"
                    )))
                }
                2 -> {
                    val response = api.getProgresoSemanalAgua()
                    emit(Result.success(DetalleHabito(
                        idHabito = 2,
                        titulo = response.tituloSeccion ?: "HIDRATACIÓN (L)",
                        metaValor = 7.04f, 
                        registros = ordenarSemanaNatural(response.datosGrafica, 2),
                        leyendaPositiva = "Verde = meta cumplida",
                        leyendaNegativa = "Rojo = meta no cumplida"
                    )))
                }
                3 -> {
                    val response = api.getProgresoSemanalEjercicio()
                    emit(Result.success(DetalleHabito(
                        idHabito = 3,
                        titulo = response.tituloSeccion ?: "DISTANCIA RECORRIDA (KM)",
                        metaValor = 8f, 
                        registros = ordenarSemanaNatural(response.datosGrafica, 3),
                        leyendaPositiva = "Verde = meta cumplida",
                        leyendaNegativa = "Rojo = meta no cumplida"
                    )))
                }
                4 -> {
                    val response = api.getProgresoSemanalPostura()
                    emit(Result.success(DetalleHabito(
                        idHabito = 4,
                        titulo = response.tituloSeccion ?: "ALERTAS DE POSTURA",
                        metaValor = 0f, 
                        registros = ordenarSemanaNatural(response.datosGrafica, 4),
                        leyendaPositiva = "", 
                        leyendaNegativa = response.mensajeMeta
                    )))
                }
                else -> {
                    val response = api.getDetalleHabito(idUsuario, idHabito)
                    emit(Result.success(response.toDomain()))
                }
            }
        } catch (e: Exception) {
            val registrosDinamicos = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom").map { dia ->
                RegistroHabito(dia, 0f, false)
            }
            emit(Result.success(DetalleHabito(idHabito, "ACTIVIDAD", 30f, "Meta cumplida", "Meta no cumplida", registrosDinamicos)))
        }
    }

    override fun getFraseAleatoria(): Flow<Result<com.knexus.ergohabit.features.progreso.domain.entities.Frase>> = flow {
        try {
            val response = api.getFraseAleatoria()
            emit(Result.success(com.knexus.ergohabit.features.progreso.domain.entities.Frase(
                id = response.idFrase,
                categoria = response.categoria,
                texto = response.texto
            )))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
