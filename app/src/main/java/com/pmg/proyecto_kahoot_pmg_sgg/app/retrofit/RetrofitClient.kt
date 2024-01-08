package com.pmg.proyecto_kahoot_pmg_sgg.app.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // URL base de la API.
    private const val BASE_URL = "https://proyectokahoot-7efa4-default-rtdb.europe-west1.firebasedatabase.app/"

    // Inicialización perezosa del ApiService. Se crea una instancia de Retrofit con la URL base
    // y el convertidor Gson para la serialización y deserialización automática de los datos.
    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}