package com.pmg.proyecto_kahoot_pmg_sgg.feature.vistaAhorcado

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.lifecycle.*
import com.pmg.proyecto_kahoot_pmg_sgg.core.data.ahorcado.model.PalabrasDTO
import com.pmg.proyecto_kahoot_pmg_sgg.core.domain.usecase.GetAhorcadoUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Author: Pablo Mata

class VistaAhorcadoViewModel : ViewModel() {

    /**
     * Contiene las palabras para el juego del ahorcado.
     */
    private val repasoModel = MutableLiveData<PalabrasDTO>()

    /**
     * Caso de uso para obtener las palabras del juego del ahorcado.
     */
    var getAhorcadoUseCase = GetAhorcadoUseCase()

    /**
     * LiveData que representa el tablero del juego.
     */
    private val _tablero = MutableLiveData<Array<Array<String>>>()
    val tablero: LiveData<Array<Array<String>>> get() = _tablero

    /**
     * Índice de la palabra actual en la lista de palabras.
     */
    private var indiceOracion = 0

    /**
     * Número de fallos del jugador.
     */
    private var fallos = 0

    /**
     * Número de aciertos del jugador.
     */
    private var aciertos = 0

    /**
     * LiveData que muestra la palabra con las letras adivinadas.
     */
    val palabraMostrar = MutableLiveData<String>()

    /**
     * Representa el estado del ahorcado (imagen).
     */
    val imagenAhorcado = MutableLiveData<Int>()

    /**
     * Indica si el jugador ha ganado el juego.
     */
    val juegoGanado = MutableLiveData(false)

    /**
     * LiveData que indica si el jugador ha perdido el juego.
     */
    val juegoPerdido = MutableLiveData(false)

    fun onCreate() {
        getAhorcado()
    }

    /**
     * Obtiene la lista de palabras del juego del ahorcado, elige una al azar y establece la palabra a mostrar.
     */
    private fun getAhorcado() {

        viewModelScope.launch {
            val result = getAhorcadoUseCase()

            if (!result.isNullOrEmpty()) {
                repasoModel.value = result[0]

                indiceOracion = (0..<11).random()
                var huecosPalabra = repasoModel.value?.palabras?.get(indiceOracion)?.length!!
                palabraMostrar.value = "_".repeat(huecosPalabra)
            }
        }
    }

    /**
     * Crea un tablero de letras organizadas en orden alfabético.
     *
     * @param filas Número de filas del tablero.
     * @param columnas Número de columnas del tablero.
     */

    fun crearTableroLetras(filas: Int, columnas: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            var letraActual = 'A'
            val nuevoTablero = Array(filas) { i ->
                Array(columnas) { j ->
                    if (i < filas - 1 || (j in 2..4)) {
                        if (letraActual <= 'Z') {
                            val letraParaUsar = letraActual.toString()
                            letraActual++
                            letraParaUsar
                        } else {
                            ""
                        }
                    } else {
                        ""
                    }
                }
            }
            _tablero.postValue(nuevoTablero)
        }
    }

    /**
     * Comprueba si la letra introducida es correcta y actualiza el tablero.
     *
     * @param letra La letra introducida por el usuario.
     * @return Devuelve `true` si la letra es correcta y `false` si es incorrecta.
     */
    fun comprobarLetraAcertada(letra: String) : Boolean {

        var letrasEncontradas = false
        var palabraDividida = repasoModel.value?.palabras?.get(indiceOracion)?.toList()

        // Comprueba si la letra introducida es correcta o no y actualiza el tablero
        for (i in palabraDividida?.indices!!) {
            if (letra == palabraDividida[i].toString()) {
                // Si la letra es correcta, la muestra en el tablero
                palabraMostrar.value = palabraMostrar.value?.replaceRange(i, i + 1, letra)?.uppercase()
                letrasEncontradas = true
                aciertos++
            }
        }

        // Si no se ha encontrado ninguna letra, se suma un fallo
        if (!letrasEncontradas) {
            fallos++
        }

        if (aciertos == palabraDividida.size) {
            juegoGanado.value = true

        } else if (fallos == 6) {
            juegoPerdido.value = true
        }

        cambiarImagenAhorcado()
        return letrasEncontradas
    }

    /**
     * Cambia la imagen del ahorcado según la cantidad de fallos.
     */
    private fun cambiarImagenAhorcado() {
        imagenAhorcado.value = fallos
    }

}