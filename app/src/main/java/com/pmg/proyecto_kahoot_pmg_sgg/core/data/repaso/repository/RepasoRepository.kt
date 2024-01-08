package com.pmg.proyecto_kahoot_pmg_sgg.core.data.repaso.repository

import com.pmg.proyecto_kahoot_pmg_sgg.app.retrofit.RetrofitClient
import com.pmg.proyecto_kahoot_pmg_sgg.core.data.repaso.model.RepasoDTO
import com.pmg.proyecto_kahoot_pmg_sgg.core.network.KahootApi

// Author: Pablo Mata

class RepasoRepository {

    private val retrofitClient = RetrofitClient.instance
    private val apiRepaso = retrofitClient.create(KahootApi::class.java)

    suspend fun getFrasesRepaso(): List<RepasoDTO> {
        val response = apiRepaso.getFrasesRepaso()
        return response.body() ?: emptyList()
    }

}