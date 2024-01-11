package com.pmg.proyecto_kahoot_pmg_sgg.feature.vistaMemory

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VistaMemoryViewModel : ViewModel() {



    private val _tablero = MutableLiveData<Array<Array<String>>>()
    val tablero: LiveData<Array<Array<String>>> get() = _tablero

    fun crearTablero(filas: Int, columnas: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            // Crea la lista de tags del 1 al 8 y repite cada tag dos veces
            val tagsDisponibles = (1..8).toMutableList().apply {
                addAll((1..8))
                shuffle()
            }

            val nuevoTablero = Array(filas) { i ->
                Array(columnas) { j ->
                    // Asigna tags de la lista a los botones
                    tagsDisponibles.removeAt(0).toString()
                }
            }
            _tablero.postValue(nuevoTablero)
        }
    }


}