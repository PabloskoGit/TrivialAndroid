package com.pmg.proyecto_kahoot_pmg_sgg.feature.vistaRepaso

import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.lifecycle.*
import com.pmg.proyecto_kahoot_pmg_sgg.R
import com.pmg.proyecto_kahoot_pmg_sgg.core.data.repaso.model.RepasoDTO
import com.pmg.proyecto_kahoot_pmg_sgg.core.domain.usecase.GetRepasoUseCase
import kotlinx.coroutines.launch

class VistaRepasoViewModel : ViewModel() {

    val repasoModel = MutableLiveData<RepasoDTO>()
    var getRepasoUseCase = GetRepasoUseCase()

    private var indiceOracion = 0
    private var aciertos = 0
    private var fallos = 0

    val juegoGanado = MutableLiveData(false)
    val juegoPerdido = MutableLiveData(false)

    fun onCreate() {
        viewModelScope.launch {
            val result = getRepasoUseCase()

            if (!result.isNullOrEmpty()) {
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


    private fun nextOracion() {

        viewModelScope.launch {
            val result = getRepasoUseCase()

            if (!result.isNullOrEmpty() && indiceOracion < result.size - 1) {

                indiceOracion++
                repasoModel.value = result[indiceOracion]
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