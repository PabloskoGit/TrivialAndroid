package com.pmg.proyecto_kahoot_pmg_sgg.feature.vistaMemory

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VistaMemoryViewModel : ViewModel() {

    private val _tablero = MutableLiveData<Array<Array<String>>>()
    val tablero: LiveData<Array<Array<String>>> get() = _tablero

    val mapaTags = mutableMapOf<String, String>()
    // En la función crearTablero de tu ViewModel

    // En la función crearTablero de tu ViewModel
// En la función crearTablero de tu ViewModel
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