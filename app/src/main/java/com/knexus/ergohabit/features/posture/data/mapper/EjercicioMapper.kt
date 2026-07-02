package com.knexus.ergohabit.features.posture.data.mapper

import com.knexus.ergohabit.core.database.entities.EjercicioEntity
import com.knexus.ergohabit.features.posture.data.models.EjercicioResponse
import com.knexus.ergohabit.features.posture.domain.entities.DashboardEjercicio

fun EjercicioResponse.toEntity(): EjercicioEntity {
    return EjercicioEntity(
        kmRecorridosText = kmRecorridosText,
        metaKmText = metaKmText,
        porcentajeCumplimiento = porcentajeCumplimiento,
        caloriasQuemadas = caloriasQuemadas,
        rachaDias = rachaDias,
        mensajeFaltanteText = mensajeFaltanteText,
        sugerenciaCaminataText = sugerenciaCaminataText,
        fraseMotivacional = fraseMotivacional
    )
}

fun EjercicioEntity.toDomain(): DashboardEjercicio {
    return DashboardEjercicio(
        kmRecorridosText = kmRecorridosText,
        metaKmText = metaKmText,
        porcentajeCumplimiento = porcentajeCumplimiento,
        caloriasQuemadas = caloriasQuemadas,
        rachaDias = rachaDias,
        mensajeFaltanteText = mensajeFaltanteText,
        sugerenciaCaminataText = sugerenciaCaminataText,
        fraseMotivacional = fraseMotivacional
    )
}

fun EjercicioResponse.toDomain(): DashboardEjercicio {
    return DashboardEjercicio(
        kmRecorridosText = kmRecorridosText,
        metaKmText = metaKmText,
        porcentajeCumplimiento = porcentajeCumplimiento,
        caloriasQuemadas = caloriasQuemadas,
        rachaDias = rachaDias,
        mensajeFaltanteText = mensajeFaltanteText,
        sugerenciaCaminataText = sugerenciaCaminataText,
        fraseMotivacional = fraseMotivacional
    )
}
