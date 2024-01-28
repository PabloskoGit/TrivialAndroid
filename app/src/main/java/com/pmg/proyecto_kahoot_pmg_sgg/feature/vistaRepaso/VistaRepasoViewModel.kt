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

    /**
     * LiveData que contiene el modelo de datos para el repaso.
     */
    val repasoModel = MutableLiveData<RepasoDTO>()

    /**
     * Caso de uso para obtener datos de repaso.
     */
    var getRepasoUseCase = GetRepasoUseCase()

    /**
     * Índice de la oración actual en el repaso.
     */
    private var indiceOracion = 0

    /**
     * Contador de aciertos durante el juego.
     */
    private var aciertos = 0

    /**
     * Contador de fallos durante el juego.
     */
    private var fallos = 0

    /**
     * LiveData que indica si el juego ha sido ganado.
     */
    val juegoGanado = MutableLiveData(false)

    /**
     * LiveData que indica si el juego ha sido perdido.
     */
    val juegoPerdido = MutableLiveData(false)

    fun onCreate() {
        viewModelScope.launch {
            val result = getRepasoUseCase()
            indiceOracion = (result.indices - 2).random()

            if (result.isNotEmpty()) {
                repasoModel.value = result[indiceOracion]
            }
        }
    }

    /**
     * Comprueba si la respuesta seleccionada es correcta y actualiza la interfaz de usuario en consecuencia.
     *
     * @param numRespuesta El número de respuesta seleccionada.
     * @param boton El botón que representa la respuesta seleccionada.
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
     * Avanza a la siguiente oración en el repaso y actualiza el ViewModel con la nueva información.
     * Utiliza el caso de uso getRepasoUseCase para obtener la lista de oraciones.
     */
    private fun nextOracion() {

        viewModelScope.launch {
            val result = getRepasoUseCase()

            if (result.isNotEmpty()) {

                indiceOracion++
                repasoModel.value = result[indiceOracion]
            }
        }
    }

    /**
     * Obtiene la lista de respuestas asociada al modelo de repaso.
     *
     * @return Lista de respuestas, o null si el modelo de repaso es nulo.
     */
    private fun getListaRespuestas(): List<String>? {
        return repasoModel.value?.respuestas
    }

    /**
     * Obtiene el índice de la respuesta correcta asociado al modelo de repaso.
     *
     * @return Índice de la respuesta correcta, o null si el modelo de repaso es nulo.
     */
    private fun getRespuestaCorrecta(): Int? {
        return repasoModel.value?.correcta
    }
}