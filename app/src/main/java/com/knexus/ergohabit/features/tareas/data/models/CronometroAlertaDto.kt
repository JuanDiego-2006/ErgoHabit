package com.knexus.ergohabit.features.tareas.data.models

import com.google.gson.annotations.SerializedName


data class CronometroAlertaDto(
    @SerializedName("idTarea") val idTarea: Int,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("requiereAlertasPostura") val requiereAlertasPostura: Boolean,
    @SerializedName("accionFisica") val accionFisica: String,
    @SerializedName("fraseMotivacional") val fraseMotivacional: String
)
