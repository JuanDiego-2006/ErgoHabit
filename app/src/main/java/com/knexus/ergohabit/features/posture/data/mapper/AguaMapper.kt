package com.knexus.ergohabit.features.posture.data.mapper

import com.knexus.ergohabit.features.posture.data.models.*
import com.knexus.ergohabit.features.posture.domain.entities.*

fun DashboardAguaResponse.toDomain() = DashboardAgua(
    metaDiariaMl = metaDiariaMl,
    consumidoHoyMl = consumidoHoyMl,
    porcentajeProgreso = porcentajeProgreso,
    vasosConsumidos = vasosConsumidos,
    mililitrosRestantes = mililitrosRestantes,
    estaturaActual = estaturaActual,
    pesoActual = pesoActual,
    historialHoy = historialHoy.map { it.toDomain() },
    fraseMotivacional = fraseMotivacional,
    tipsHidratacion = tipsHidratacion
)

fun TomaCronologica.toDomain() = TomaAgua(
    id = id,
    cantidadMl = cantidadMl,
    hora = hora
)

fun HistorialHabitoResponse.toDomain() = RegistroSemanalAgua(
    tituloSeccion = tituloSeccion,
    mensajeMeta = mensajeMeta,
    datosGrafica = datosGrafica.map { it.toDomain() }
)

fun ElementoBarraGrafica.toDomain() = ElementoGraficaAgua(
    diaSemana = diaSemana,
    valor = valor,
    metaCumplida = metaCumplida,
    esHoy = esHoy
)
