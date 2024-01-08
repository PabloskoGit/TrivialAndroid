package com.pmg.proyecto_kahoot_pmg_sgg.core.data.preguntas.repository

import com.pmg.proyecto_kahoot_pmg_sgg.app.retrofit.RetrofitClient
import com.pmg.proyecto_kahoot_pmg_sgg.core.data.preguntas.model.PreguntaDTO
import com.pmg.proyecto_kahoot_pmg_sgg.core.network.KahootApi

class PreguntasRepository {

    private val retrofitClient = RetrofitClient.instance
    private val apiQuiz = retrofitClient.create(KahootApi::class.java)

    suspend fun obtenerPreguntas(): List<PreguntaDTO> {
        val response = apiQuiz.obtenerPreguntas()
        return response.body() ?: emptyList()
    }
}