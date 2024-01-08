package com.pmg.proyecto_kahoot_pmg_sgg.core.network

import com.pmg.proyecto_kahoot_pmg_sgg.core.data.ahorcado.model.PalabrasDTO
import com.pmg.proyecto_kahoot_pmg_sgg.core.data.pregfinal.model.PreguntaFinalDTO
import com.pmg.proyecto_kahoot_pmg_sgg.core.data.preguntas.model.PreguntaDTO
import com.pmg.proyecto_kahoot_pmg_sgg.core.data.repaso.model.RepasoDTO
import retrofit2.Response
import retrofit2.http.GET

interface KahootApi {

    @GET("ahorcado.json")
    suspend fun getPalabras(): Response<List<PalabrasDTO>>

    @GET("repaso_educacion_fisica.json")
    suspend fun getFrasesRepaso(): Response<List<RepasoDTO>>

    @GET("preguntas_educacion_fisica.json")
    suspend fun obtenerPreguntas(): Response<List<PreguntaDTO>>

    @GET("prueba_final.json")
    suspend fun obtenerPregFinal(): Response<List<PreguntaFinalDTO>>

}