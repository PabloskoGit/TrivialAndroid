package com.pmg.proyecto_kahoot_pmg_sgg.core.domain.usecase

import com.pmg.proyecto_kahoot_pmg_sgg.core.data.ahorcado.model.PalabrasDTO
import com.pmg.proyecto_kahoot_pmg_sgg.core.data.ahorcado.repository.AhorcadoRepository

class GetAhorcadoUseCase {

    private val ahorcadoRepository = AhorcadoRepository()
    suspend operator fun invoke(): List<PalabrasDTO> = ahorcadoRepository.getPalabras()

}