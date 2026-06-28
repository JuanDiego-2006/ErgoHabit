package com.knexus.ergohabit.features.progreso.data.repositories

import com.knexus.ergohabit.core.database.dao.ProgresoDao
import com.knexus.ergohabit.features.progreso.data.datasource.api.ProgresoApi
import com.knexus.ergohabit.features.progreso.data.mapper.toDomain
import com.knexus.ergohabit.features.progreso.data.mapper.toEntity
import com.knexus.ergohabit.features.progreso.data.models.DatoGraficaDto
import com.knexus.ergohabit.features.progreso.domain.entities.DetalleHabito
import com.knexus.ergohabit.features.progreso.domain.entities.HabitoProgreso
import com.knexus.ergohabit.features.progreso.domain.entities.ProgresoDia
import com.knexus.ergohabit.features.progreso.domain.entities.RegistroHabito
import com.knexus.ergohabit.features.progreso.domain.repositories.ProgresoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.util.Calendar
import javax.inject.Inject

class ProgresoRepositoryImpl @Inject constructor(
    private val api: ProgresoApi,
    private val dao: ProgresoDao
) : ProgresoRepository {

    override fun getHabitosProgreso(idUsuario: Int): Flow<Result<List<HabitoProgreso>>> = flow {
        // 1. Emitir datos locales (Carga instantánea)
        val local = dao.getAllHabitos().first()
        if (local.isNotEmpty()) {
            emit(Result.success(local.map { it.toDomain() }))
        }

        try {
            // 2. Carga desde API
            val response = api.getHabitosProgreso(idUsuario)
            val domainHabitos = response.map { it.toDomain() }
            
            // 3. Sincronizar Room
            dao.insertHabitos(domainHabitos.map { it.toEntity() })
            
            emit(Result.success(domainHabitos))
        } catch (e: Exception) {
            if (local.isEmpty()) emit(Result.failure(e))
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
        // 1. Intentar cargar de Room primero
        val habitBase = dao.getHabitoById(idHabito)
        val localRegistros = dao.getRegistrosByHabito(idHabito).first()
        
        if (habitBase != null && localRegistros.isNotEmpty()) {
            emit(Result.success(DetalleHabito(
                idHabito = idHabito,
                titulo = habitBase.tituloSeccion ?: habitBase.nombre.uppercase(),
                metaValor = when(idHabito){ 2 -> 7.04f; else -> 8f }, // Meta visual orientativa
                leyendaPositiva = habitBase.leyendaPositiva ?: "Verde = meta cumplida",
                leyendaNegativa = habitBase.leyendaNegativa ?: "Rojo = meta no cumplida",
                registros = localRegistros.map { it.toDomain() }
            )))
        }

        try {
            val response = when (idHabito) {
                1 -> api.getProgresoSemanalSueno()
                2 -> api.getProgresoSemanalAgua()
                3 -> api.getProgresoSemanalEjercicio()
                4 -> api.getProgresoSemanalPostura()
                else -> throw Exception("Hábito no soportado")
            }

            val registrosOrdenados = ordenarSemanaNatural(response.datosGrafica, idHabito)
            
            val detalle = DetalleHabito(
                idHabito = idHabito,
                titulo = response.tituloSeccion ?: "DETALLE",
                metaValor = when(idHabito){ 2 -> 7.04f; else -> 8f },
                leyendaPositiva = if (idHabito == 4) "" else "Verde = meta cumplida",
                leyendaNegativa = if (idHabito == 4) response.mensajeMeta else "Rojo = meta no cumplida",
                registros = registrosOrdenados
            )

            // 2. Actualizar Room de forma atómica
            val habitEntity = dao.getHabitoById(idHabito) ?: HabitoProgreso(idHabito, "", "", "", 0).toEntity()
            dao.updateDetalleHabito(
                idHabito, 
                habitEntity.copy(
                    tituloSeccion = detalle.titulo,
                    leyendaPositiva = detalle.leyendaPositiva,
                    leyendaNegativa = detalle.leyendaNegativa
                ),
                registrosOrdenados.map { it.toEntity(idHabito) }
            )

            emit(Result.success(detalle))
        } catch (e: Exception) {
            if (localRegistros.isEmpty()) emit(Result.failure(e))
        }
    }

    override fun getFraseAleatoria(): Flow<Result<com.knexus.ergohabit.features.progreso.domain.entities.Frase>> = flow {
        // 1. Carga instantánea desde Room
        val local = dao.getFraseDia().first()
        if (local != null) {
            emit(Result.success(local.toDomain()))
        }

        try {
            // 2. Fetch de la API
            val response = api.getFraseAleatoria()
            val frase = com.knexus.ergohabit.features.progreso.domain.entities.Frase(
                id = response.idFrase,
                categoria = response.categoria,
                texto = response.texto
            )
            
            // 3. Sincronizar Room
            dao.insertFrase(frase.toEntity())
            
            emit(Result.success(frase))
        } catch (e: Exception) {
            if (local == null) emit(Result.failure(e))
        }
    }
}
