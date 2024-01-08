package com.pmg.proyecto_kahoot_pmg_sgg.core.data.repaso.model

import com.google.gson.annotations.SerializedName

// Author: Pablo Mata

data class RepasoDTO(

    val oracion: String,
    @SerializedName("opciones")
    val respuestas: List<String>,
    val correcta: Int

)
