package com.pmg.proyecto_kahoot_pmg_sgg.feature.vistaJuego

import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.lifecycle.*
import com.pmg.proyecto_kahoot_pmg_sgg.R
import com.pmg.proyecto_kahoot_pmg_sgg.core.data.preguntas.model.PreguntaDTO
import com.pmg.proyecto_kahoot_pmg_sgg.core.domain.usecase.GetPreguntasUseCase
import kotlinx.coroutines.launch

// Author: Pablo Mata

class VistaJuegoViewModel : ViewModel() {

    /**
     * Modelo LiveData que contiene la pregunta actual.
     */
    val preguntaDTOModel = MutableLiveData<PreguntaDTO>()

    /**
     * Caso de uso para obtener preguntas.
     */
    var getPreguntaUseCase = GetPreguntasUseCase()

    /**
     * Índice de la pregunta actual.
     */
    private var indicePregunta = 0

    /**
     * Contador de aciertos.
     */
    private var aciertos = 0

    /**
     * Contador de fallos.
     */
    private var fallos = 0

    /**
     * LiveData para indicar si el juego fue ganado.
     */
    val juegoGanado = MutableLiveData(false)

    /**
     * LiveData para indicar si el juego fue perdido.
     */
    val juegoPerdido = MutableLiveData(false)

    fun onCreate() {
        viewModelScope.launch {
            val result = getPreguntaUseCase()
            indicePregunta = (result.indices - 5).random()

            if (!result.isNullOrEmpty()) {
                preguntaDTOModel.value = result[indicePregunta]
            }
        }
    }

    /**
     * Función para comprobar si la respuesta seleccionada es correcta.
     *
     * @param numRespuesta Índice de la respuesta seleccionada.
     * @param boton Botón que representa la respuesta seleccionada.
     */
    fun comprobarRespuestaAcertada(numRespuesta: Int, boton: Button) {

        val respuestaSeleccionada = getListaRespuestas()?.get(numRespuesta)

        // Encuentra el índice de la respuesta seleccionada
        val indiceRespuesta = getListaRespuestas()?.indexOf(respuestaSeleccionada)

        if (indiceRespuesta != null && indiceRespuesta == getRespuestaCorrecta()) {

            //Suma un acierto
            aciertos++
            boton.setBackgroundResource(R.drawable.background_boton_acierto)

            if (aciertos == 5) {
                juegoGanado.value = true
            } else {

                // Independientemente de la respuesta, avanza a la siguiente pregunta
                // Programar una tarea para pasar a la siguiente pregunta después de 3 segundos
                Handler(Looper.getMainLooper()).postDelayed({

                    boton.setBackgroundResource(R.drawable.background_botones_juego_design)
                    nextOracion()

                }, 1500)
            }

        } else {
            //Suma un fallo
            fallos++
            boton.setBackgroundResource(R.drawable.background_boton_error)

            if (fallos == 1) {
                juegoPerdido.value = true
            }
        }

    }

    /**
     * Función para avanzar a la siguiente pregunta.
     */
    private fun nextOracion() {

        viewModelScope.launch {
            val result = getPreguntaUseCase()

            if (!result.isNullOrEmpty() && indicePregunta < result.size - 1) {

                indicePregunta++
                preguntaDTOModel.value = result[indicePregunta]
            }
        }
    }

    /**
     * Obtiene la lista de respuestas de la pregunta actual.
     *
     * @return Lista de respuestas o null si no hay pregunta cargada.
     */
    private fun getListaRespuestas(): List<String>? {
        return preguntaDTOModel.value?.respuestas
    }

    /**
     * Obtiene el número de respuesta correcta de la pregunta actual.
     *
     * @return Número de respuesta correcta o null si no hay pregunta cargada.
     */
    private fun getRespuestaCorrecta(): Int? {
        return preguntaDTOModel.value?.correcta
    }
}