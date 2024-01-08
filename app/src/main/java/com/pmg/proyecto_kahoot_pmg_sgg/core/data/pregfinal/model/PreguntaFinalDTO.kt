package com.pmg.proyecto_kahoot_pmg_sgg.core.data.pregfinal.model

import com.google.gson.annotations.SerializedName

// Author: Pablo Mata

data class PreguntaFinalDTO(
    val oracion: String,
    @SerializedName("opciones")
    val respuestas: List<String>,
    val correcta: Int
)
