package com.pmg.proyecto_kahoot_pmg_sgg.core.data.ahorcado.repository

import com.pmg.proyecto_kahoot_pmg_sgg.app.retrofit.RetrofitClient
import com.pmg.proyecto_kahoot_pmg_sgg.core.data.ahorcado.model.PalabrasDTO
import com.pmg.proyecto_kahoot_pmg_sgg.core.network.KahootApi

class AhorcadoRepository {

    private val retrofitClient = RetrofitClient.instance
    private val apiQuiz = retrofitClient.create(KahootApi::class.java)

    // Función para obtener las preguntas. Se ejecuta en un hilo secundario.
    suspend fun getPalabras(): List<PalabrasDTO> {
        // Se obtiene la respuesta de la API. Si es nula, se devuelve una lista vacía. Si no, se devuelve la lista de preguntas.
        val response = apiQuiz.getPalabras()
        return response.body() ?: emptyList()
    }

}