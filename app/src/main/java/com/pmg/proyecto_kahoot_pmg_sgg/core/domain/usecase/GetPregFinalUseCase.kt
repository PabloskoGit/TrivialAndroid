package com.pmg.proyecto_kahoot_pmg_sgg.core.domain.usecase

import com.pmg.proyecto_kahoot_pmg_sgg.core.data.pregfinal.model.PreguntaFinalDTO
import com.pmg.proyecto_kahoot_pmg_sgg.core.data.pregfinal.repository.PregFinalRepository

// Author: Pablo Mata

class GetPregFinalUseCase {

    private val pregFinalRepository= PregFinalRepository()
    suspend operator fun invoke(): List<PreguntaFinalDTO> = pregFinalRepository.getPreguntasFinales()
}