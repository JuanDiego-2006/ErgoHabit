package com.knexus.ergohabit.features.posture.data.mapper

import com.knexus.ergohabit.core.database.entities.AguaDashboardEntity
import com.knexus.ergohabit.features.posture.data.models.*
import com.knexus.ergohabit.features.posture.domain.entities.*

fun DashboardAguaResponse.toDomain() = DashboardAgua(
    metaDiariaMl = this.metaDiariaMl,
    consumidoHoyMl = this.consumidoHoyMl,
    porcentajeProgreso = this.porcentajeProgreso,
    vasosConsumidos = this.vasosConsumidos,
    mililitrosRestantes = this.mililitrosRestantes,
    estaturaActual = this.estaturaActual,
    pesoActual = this.pesoActual,
    historialHoy = this.historialHoy.map { it.toDomain() },
    fraseMotivacional = this.fraseMotivacional,
    tipsHidratacion = this.tipsHidratacion
)

fun DashboardAgua.toEntity() = AguaDashboardEntity(
    id = 1,
    metaDiariaMl = this.metaDiariaMl,
    consumidoHoyMl = this.consumidoHoyMl,
    porcentajeProgreso = this.porcentajeProgreso,
    vasosConsumidos = this.vasosConsumidos,
    mililitrosRestantes = this.mililitrosRestantes,
    fraseMotivacional = this.fraseMotivacional,
    pesoActual = this.pesoActual,
    estaturaActual = this.estaturaActual
)

fun AguaDashboardEntity.toDomainDashboard() = DashboardAgua(
    metaDiariaMl = this.metaDiariaMl,
    consumidoHoyMl = this.consumidoHoyMl,
    porcentajeProgreso = this.porcentajeProgreso,
    vasosConsumidos = this.vasosConsumidos,
    mililitrosRestantes = this.mililitrosRestantes,
    estaturaActual = this.estaturaActual,
    pesoActual = this.pesoActual,
    historialHoy = emptyList(),
    fraseMotivacional = this.fraseMotivacional,
    tipsHidratacion = emptyList()
)

fun TomaCronologica.toDomain() = TomaAgua(
    id = this.id,
    cantidadMl = this.cantidadMl,
    hora = this.hora
)

fun AguaHistorialHabitoResponse.toDomain() = RegistroSemanalAgua(
    tituloSeccion = this.tituloSeccion,
    mensajeMeta = this.mensajeMeta,
    datosGrafica = this.datosGrafica.map { it.toDomain() }
)

fun ElementoBarraGrafica.toDomain() = ElementoGraficaAgua(
    diaSemana = this.diaSemana,
    valor = this.valor,
    metaCumplida = this.metaCumplida,
    esHoy = this.esHoy
)
