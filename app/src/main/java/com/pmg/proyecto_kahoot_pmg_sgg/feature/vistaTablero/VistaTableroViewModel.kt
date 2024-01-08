package com.pmg.proyecto_kahoot_pmg_sgg.feature.vistaTablero

import androidx.lifecycle.*
import com.pmg.proyecto_kahoot_pmg_sgg.core.domain.model.jugador.Jugador
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.List
import kotlin.collections.find
import kotlin.collections.forEach
import kotlin.collections.listOf
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.set

/**
 * ViewModel para la lógica de la vista del tablero.
 */
class VistaTableroViewModel : ViewModel() {


    private val _tablero = MutableLiveData<Array<Array<String>>>()
    val tablero: LiveData<Array<Array<String>>> get() = _tablero

    private val _jugadores = MutableLiveData<List<Jugador>>()
    val jugadores: LiveData<List<Jugador>> get() = _jugadores

    private val _jugadorActual = MutableLiveData<Int>()
    val jugadorActual: LiveData<Int> get() = _jugadorActual

    var jugador: Int = 1

    // Mapa que asocia ID de jugador con su LiveData de posición
    private val mapPosicionesJugadores = mutableMapOf<Int, MutableLiveData<Pair<Int, Int>>>()

    init {
        // Inicializar aquí la lista de jugadores
        _jugadores.value = listOf(
            Jugador(id = 1, posicion = Pair(0, 0), direccion = "DERECHA"),
            Jugador(id = 2, posicion = Pair(0, 0), direccion = "DERECHA")
            // Puedes agregar más jugadores según sea necesario
        )
    }


    fun crearTablero(filas: Int, columnas: Int) {
        _jugadorActual.postValue(jugador)
        viewModelScope.launch(Dispatchers.Default) {
            val nuevoTablero = Array(filas) { i ->
                Array(columnas) { j ->
                    if (i == 0 || i == filas - 1 || j == 0 || j == columnas - 1) {
                        (1 + (i + j) % 4).toString()
                    } else if (i == filas / 2 && j == columnas / 2) {
                        "5"
                    } else {
                        ""
                    }
                }
            }
            _tablero.postValue(nuevoTablero)
        }
    }

    fun moverJugador(numeroCasillas: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            val jugadorActual = _jugadores.value?.find { it.id == _jugadorActual.value }
            jugadorActual?.let { jugador ->
                if (jugador.haCompletadoPreguntas(1, 2, 3, 4)) {
                    // Mueve al jugador a la posición (3,3)
                    jugador.posicion = Pair(3, 3)
                } else {
                    // Obtén la posición actual del jugador
                    var (nuevaFila, nuevaColumna) = jugador.posicion

                    // Obtén el tablero actual del LiveData
                    val tablero = tablero.value ?: return@launch

                    // Itera según el número de casillas que el jugador debe avanzar
                    repeat(numeroCasillas) {
                        // Obtiene la dirección actual del jugador
                        val direccionActual = Direccion.valueOf(jugador.direccion)

                        // Realiza acciones basadas en la dirección actual del jugador
                        when (direccionActual) {
                            Direccion.DERECHA -> {
                                nuevaColumna++
                                if (nuevaColumna >= tablero[0].size - 1 || tablero[nuevaFila][nuevaColumna].isEmpty()) {
                                    jugador.direccion = Direccion.ABAJO.name
                                }
                            }

                            Direccion.ABAJO -> {
                                nuevaFila++
                                if (nuevaFila >= tablero.size - 1 || tablero[nuevaFila][nuevaColumna].isEmpty()) {
                                    jugador.direccion = Direccion.IZQUIERDA.name
                                }
                            }

                            Direccion.IZQUIERDA -> {
                                nuevaColumna--
                                if (nuevaColumna < 1 || tablero[nuevaFila][nuevaColumna].isEmpty()) {
                                    jugador.direccion = Direccion.ARRIBA.name
                                }
                            }

                            Direccion.ARRIBA -> {
                                nuevaFila--
                                if (nuevaFila < 1 || tablero[nuevaFila][nuevaColumna].isEmpty()) {
                                    jugador.direccion = Direccion.DERECHA.name
                                }
                            }
                        }
                    }


                    // Actualiza la posición del jugador en el ViewModel
                    jugador.posicion = Pair(nuevaFila, nuevaColumna)
                }

                    // Llama a la función para notificar a la vista sobre el cambio en la posición del jugador
                    actualizarPosicionJugador(jugador.id, jugador.posicion)

            }
        }
    }

    fun cambiarJugador() {
        _jugadorActual.value?.let { jugadorActual ->
            val nuevoJugador = (jugadorActual % (_jugadores.value?.size ?: 2)) + 1
            _jugadorActual.postValue(nuevoJugador)
            jugador = nuevoJugador
        }
    }


    // Método para obtener el LiveData de posición de un jugador
    fun getPosicionJugadorLiveData(jugadorId: Int): MutableLiveData<Pair<Int, Int>> {
        if (!mapPosicionesJugadores.containsKey(jugadorId)) {
            // Si el MutableLiveData no existe, créalo y agréguelo al mapa
            mapPosicionesJugadores[jugadorId] = MutableLiveData()
        }
        return mapPosicionesJugadores[jugadorId]!!
    }

    // Método para actualizar la posición de un jugador
    fun actualizarPosicionJugador(jugadorId: Int, nuevaPosicion: Pair<Int, Int>) {
        // Obtén el MutableLiveData del jugador y actualiza su valor
        getPosicionJugadorLiveData(jugadorId).postValue(nuevaPosicion)
    }

    fun actualizarPuntosJugador(
        jugadorId: Int,
        juego1: Boolean,
        juego2: Boolean,
        juego3: Boolean,
        juego4: Boolean,
        juego5: Boolean
    ) {
        // Obtén el jugador correspondiente al jugadorId
        val jugador = _jugadores.value?.find { it.id == jugadorId }

        // Verifica que el jugador no sea nulo
        jugador?.let {
            // Actualiza los juegos completados del jugador según los valores booleanos
            if (juego1) jugador.agregarJuegoCompleto("1")
            if (juego2) jugador.agregarJuegoCompleto("2")
            if (juego3) jugador.agregarJuegoCompleto("3")
            if (juego4) jugador.agregarJuegoCompleto("4")
            if (juego5) {
                jugador.agregarJuegoCompleto("5")

                // Verifica si el jugador ha completado los 5 juegos
                if (jugador.obtenerJuegosCompletados().size == 5) {
                    // Notifica que el jugador ha ganado y restablece la partida
                    jugador.esVictoria(true)
                }
            }
        }
    }

    fun restablecerPartida() {
        // Limpia los datos de juegos completados, id, posicion y direccion de todos los jugadores
        _jugadores.value?.forEach { jugador ->
            jugador.juegosCompletados.clear()
            jugador.esVictoria(false)
            // Inicializar aquí la lista de jugadores
            _jugadores.value = listOf(
                Jugador(id = 1, posicion = Pair(0, 0), direccion = "DERECHA"),
                Jugador(id = 2, posicion = Pair(0, 0), direccion = "DERECHA")
                // Puedes agregar más jugadores según sea necesario
            )  }

        _jugadorActual.postValue(1)




    }

    fun actualizarTextoPuntosJugador(jugadorId: Int): String {
        // Obtén el jugador correspondiente al jugadorId
        val jugador = _jugadores.value?.find { it.id == jugadorId }

        // Verifica que el jugador no sea nulo
        jugador?.let {

            // Retorna el texto generado por mostrarJuegosCompletados
            return jugador.mostrarJuegosCompletados()
        }

        // Retorna un texto por defecto si el jugador es nulo
        return "Jugador no encontrado"
    }
    fun obtenerJugadorVictoria(): Boolean {
        // Obtén el jugador actual
        val jugadorActual = _jugadores.value?.find { it.id == _jugadorActual.value }

        // Verifica que el jugador no sea nulo y retorna su estado de victoria
        return jugadorActual?.obtenerVictoria() ?: false
    }

    fun obtenerPosicionesTodosJugadores(): List<Pair<Int, Int>> {
        val posiciones = mutableListOf<Pair<Int, Int>>()
        jugadores.value?.forEach { jugador ->
            // Observa los cambios en la posición del jugador actual
            val posicion = getPosicionJugadorLiveData(jugador.id).value
            if (posicion != null) {
                posiciones.add(posicion)
            }
        }
        return posiciones
    }
    /*fun checkMinijuego(casilla: Int, jugador: Int, haFallado: Boolean) : Boolean{

        // Comprueba si el minijuego ya ha sido completado por el jugador. Si es así, no se ejecuta
        if (jugador == 1) {
            if (checkMinijuegos1[casilla - 1]) {
                return false
            } else {
                checkMinijuegos1[casilla - 1] = true
                return true
            }
        } else if (jugador == 2) {
            if (checkMinijuegos2[casilla - 1]) {
                return false
            } else {
                checkMinijuegos2[casilla - 1] = true
                return true
            }
        }

        return false
    }*/


    // Enumeración para representar las direcciones posibles
    private enum class Direccion {
        DERECHA, ABAJO, IZQUIERDA, ARRIBA
    }

}