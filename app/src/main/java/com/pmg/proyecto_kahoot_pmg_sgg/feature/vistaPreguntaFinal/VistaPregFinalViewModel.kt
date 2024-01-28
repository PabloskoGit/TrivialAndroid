package com.pmg.proyecto_kahoot_pmg_sgg.feature.vistaPreguntaFinal

import android.widget.Button
import androidx.lifecycle.*
import com.pmg.proyecto_kahoot_pmg_sgg.R
import com.pmg.proyecto_kahoot_pmg_sgg.core.data.pregfinal.model.PreguntaFinalDTO
import com.pmg.proyecto_kahoot_pmg_sgg.core.domain.usecase.GetPregFinalUseCase
import kotlinx.coroutines.launch

// Author: Pablo Mata

class VistaPregFinalViewModel : ViewModel(){

    /**
     * LiveData que contiene el modelo de la Pregunta Final.
     * Se actualiza con la información de la pregunta actual.
     */
    val repasoModel = MutableLiveData<PreguntaFinalDTO>()

    /**
     * Caso de uso utilizado para obtener información sobre la Pregunta Final.
     */
    var getPregFinalUseCase = GetPregFinalUseCase()

    /**
     * Índice de la pregunta actual en el modelo de la Pregunta Final.
     * Se utiliza para navegar entre diferentes preguntas.
     */
    private var indiceOracion = 0

    /**
     * Número de respuestas correctas en el juego de la Pregunta Final.
     */
    private var aciertos = 0

    /**
     * Número de respuestas incorrectas en el juego de la Pregunta Final.
     */
    private var fallos = 0

    /**
     * LiveData que indica si el jugador ha ganado el juego de la Pregunta Final.
     */
    val juegoGanado = MutableLiveData(false)

    /**
     * LiveData que indica si el jugador ha perdido el juego de la Pregunta Final.
     */
    val juegoPerdido = MutableLiveData(false)

    fun onCreate() {
        viewModelScope.launch {
            val result = getPregFinalUseCase()
            indiceOracion = (result.indices - 1).random()

            if (result.isNotEmpty()) {
                repasoModel.value = result[indiceOracion]
            }
        }
    }

    /**
     * Comprueba si la respuesta seleccionada es correcta.
     * Actualiza la interfaz gráfica al resaltar el botón con un fondo específico.
     * Además, incrementa los contadores de aciertos y fallos, y notifica sobre el resultado del juego.
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

            if (aciertos == 1) {
                juegoGanado.value = true
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
     * Obtiene la lista de respuestas del modelo de repaso actual.
     *
     * @return Lista de respuestas o null si el modelo de repaso es nulo.
     */
    private fun getListaRespuestas(): List<String>? {
        return repasoModel.value?.respuestas
    }

    /**
     * Obtiene el índice de la respuesta correcta del modelo de repaso actual.
     *
     * @return Índice de la respuesta correcta o null si el modelo de repaso es nulo.
     */
    private fun getRespuestaCorrecta(): Int? {
        return repasoModel.value?.correcta
    }
}