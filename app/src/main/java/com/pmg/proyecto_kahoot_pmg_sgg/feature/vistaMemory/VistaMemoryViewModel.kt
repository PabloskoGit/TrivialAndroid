package com.pmg.proyecto_kahoot_pmg_sgg.feature.vistaMemory

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VistaMemoryViewModel : ViewModel() {

    /**
     * LiveData que contiene la representación del tablero de juego.
     * Cada elemento de la matriz contiene el contenido de una casilla en el tablero.
     * Puede ser observado para actualizar la interfaz de usuario cuando cambia el tablero.
     */
    private val _tablero = MutableLiveData<Array<Array<String>>>()
    val tablero: LiveData<Array<Array<String>>> get() = _tablero

    /**
     * Mapa que asocia los tags de las casillas del tablero con su contenido.
     * Se utiliza para almacenar la relación entre el tag de una casilla y el contenido que representa.
     * Puede ser útil para realizar consultas sobre el contenido de una casilla específica en el tablero.
     */
    private val mapaTags = mutableMapOf<String, String>()

    /**
     * Crea un nuevo tablero de juego con el número especificado de filas y columnas.
     * Cada mitad del tablero contiene números del 1 al 8 en posiciones aleatorias.
     * La representación del tablero se almacena en [_tablero].
     *
     * @param filas Número de filas en el tablero.
     * @param columnas Número de columnas en el tablero.
     */
    fun crearTablero(filas: Int, columnas: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            // Crea las listas de tags del 1 al 8 para cada mitad del tablero
            val tagsNumerosPrimeraMitad = (1..8).toList().shuffled()
            val tagsNumerosSegundaMitad = (1..8).toList().shuffled()

            // Combina las dos listas para tener una lista total de tags
            val tagsNumeros = tagsNumerosPrimeraMitad + tagsNumerosSegundaMitad

            val nuevoTablero = Array(filas) { i ->
                Array(columnas) { j ->
                    val tag = tagsNumeros[i * columnas + j].toString()
                    val contenido = if (i < filas / 2) {
                        // Los primeros 8 botones tendrán texto
                        tag
                    } else {
                        // Los siguientes 8 botones tendrán imágenes
                        tag
                    }

                    // Almacenar el tag y contenido en el mapa
                    mapaTags[tag] = contenido

                    contenido
                }
            }

            _tablero.postValue(nuevoTablero)
        }
    }
}