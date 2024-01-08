package com.pmg.proyecto_kahoot_pmg_sgg.core.data.preguntas.model

// Author: Pablo Mata

data class PreguntaDTO(
   val pregunta: String,
   val respuestas: List<String>,
   val correcta: Int
)
