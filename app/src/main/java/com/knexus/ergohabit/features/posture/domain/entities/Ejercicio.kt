package com.knexus.ergohabit.features.posture.domain.entities

data class DashboardEjercicio(
    val kmRecorridosText: String,
    val metaKmText: String,
    val porcentajeCumplimiento: Int,
    val caloriasQuemadas: Int,
    val rachaDias: Int,
    val mensajeFaltanteText: String,
    val sugerenciaCaminataText: String,
    val fraseMotivacional: String
)
