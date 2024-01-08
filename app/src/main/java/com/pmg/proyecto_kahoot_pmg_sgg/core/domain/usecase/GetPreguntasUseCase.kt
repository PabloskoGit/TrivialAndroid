package com.pmg.proyecto_kahoot_pmg_sgg.core.domain.usecase

import com.pmg.proyecto_kahoot_pmg_sgg.core.data.preguntas.model.PreguntaDTO
import com.pmg.proyecto_kahoot_pmg_sgg.core.data.preguntas.repository.PreguntasRepository

// Author: Pablo Mata

class GetPreguntasUseCase {

    private val preguntasRepository = PreguntasRepository()
    suspend operator fun invoke(): List<PreguntaDTO> = preguntasRepository.obtenerPreguntas()
}