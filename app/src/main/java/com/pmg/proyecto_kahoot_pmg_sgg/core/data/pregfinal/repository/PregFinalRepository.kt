package com.pmg.proyecto_kahoot_pmg_sgg.core.data.pregfinal.repository

import com.pmg.proyecto_kahoot_pmg_sgg.app.retrofit.RetrofitClient
import com.pmg.proyecto_kahoot_pmg_sgg.core.data.pregfinal.model.PreguntaFinalDTO
import com.pmg.proyecto_kahoot_pmg_sgg.core.network.KahootApi

// Author: Pablo Mata

class PregFinalRepository {

    private val retrofitClient = RetrofitClient.instance
    private val apiQuiz = retrofitClient.create(KahootApi::class.java)

    suspend fun getPreguntasFinales(): List<PreguntaFinalDTO> {
        val response = apiQuiz.obtenerPregFinal()
        return response.body() ?: emptyList()
    }

}