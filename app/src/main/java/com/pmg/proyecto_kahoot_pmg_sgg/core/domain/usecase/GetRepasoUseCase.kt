package com.pmg.proyecto_kahoot_pmg_sgg.core.domain.usecase

import com.pmg.proyecto_kahoot_pmg_sgg.core.data.repaso.model.RepasoDTO
import com.pmg.proyecto_kahoot_pmg_sgg.core.data.repaso.repository.RepasoRepository

// Author: Pablo Mata

class GetRepasoUseCase {

    private val repasoRepository = RepasoRepository()
    suspend operator fun invoke(): List<RepasoDTO> = repasoRepository.getFrasesRepaso()
}