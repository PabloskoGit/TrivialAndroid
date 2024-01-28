package com.pmg.proyecto_kahoot_pmg_sgg.feature.vistaPreguntaFinal

import android.widget.Button
import androidx.lifecycle.*
import com.pmg.proyecto_kahoot_pmg_sgg.R
import com.pmg.proyecto_kahoot_pmg_sgg.core.data.pregfinal.model.PreguntaFinalDTO
import com.pmg.proyecto_kahoot_pmg_sgg.core.domain.usecase.GetPregFinalUseCase
import kotlinx.coroutines.launch

// Author: Pablo Mata

class VistaPregFinalViewModel : ViewModel(){

    val repasoModel = MutableLiveData<PreguntaFinalDTO>()
    var getPregFinalUseCase = GetPregFinalUseCase()

    private var indiceOracion = 0
    private var aciertos = 0
    private var fallos = 0

    val juegoGanado = MutableLiveData(false)
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

    // Obtiene la lista de preguntas
    private fun getListaRespuestas(): List<String>? {
        return repasoModel.value?.respuestas
    }
    // Obtiene el numero de respuesta correcta
    private fun getRespuestaCorrecta(): Int? {
        return repasoModel.value?.correcta
    }
}