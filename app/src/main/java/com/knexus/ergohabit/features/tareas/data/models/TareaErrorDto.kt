package com.knexus.ergohabit.features.tareas.data.models

import com.google.gson.annotations.SerializedName

data class TareaErrorDto(
    @SerializedName("code") val code: String? = null,
    @SerializedName("message") val message: String? = null
)
