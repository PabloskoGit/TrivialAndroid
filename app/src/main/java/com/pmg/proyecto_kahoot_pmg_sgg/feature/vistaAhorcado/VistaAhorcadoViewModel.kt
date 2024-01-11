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

    val repasoModel = MutableLiveData<PalabrasDTO>()
    var getAhorcadoUseCase = GetAhorcadoUseCase()

    private val _tablero = MutableLiveData<Array<Array<String>>>()
    val tablero: LiveData<Array<Array<String>>> get() = _tablero

    private var indiceOracion = 0
    private var fallos = 0
    private var aciertos = 0
    val palabraMostrar = MutableLiveData<String>()
    val imagenAhorcado = MutableLiveData<Int>()

    val juegoGanado = MutableLiveData(false)
    val juegoPerdido = MutableLiveData(false)

    fun onCreate() {
        getAhorcado()
    }

    // Obtiene la lista de palabras, y segun el indice, obtiene la palabra que quieras, y luego la divide en letras
    private fun getAhorcado() {

        viewModelScope.launch {
            val result = getAhorcadoUseCase()
            indiceOracion = (result.indices).random()

            if (!result.isNullOrEmpty()) {
                repasoModel.value = result[0]
                var huecosPalabra = repasoModel.value?.palabras?.get(indiceOracion)?.length!!
                palabraMostrar.value = "_".repeat(huecosPalabra)
            }
        }
    }

    // Crea un tablero de letras, recibe como parametros el numero de filas y columnas, las letras van ordenadas en oren alfabetico
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

    // Gestiona la letra ya dividida en letras, obtiene como parametros el contenido de un editText
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
    private fun cambiarImagenAhorcado() {
        imagenAhorcado.value = fallos
    }

}