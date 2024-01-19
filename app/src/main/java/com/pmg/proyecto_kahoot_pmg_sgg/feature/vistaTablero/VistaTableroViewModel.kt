package com.pmg.proyecto_kahoot_pmg_sgg.feature.vistaTablero

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.*
import com.example.t8_ej01_persistenciadatossqlite.DatabaseHelper
import com.pmg.proyecto_kahoot_pmg_sgg.app.MainActivity
import com.pmg.proyecto_kahoot_pmg_sgg.core.domain.model.jugador.Jugador
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private lateinit var databaseHelper: DatabaseHelper



    init {
        // Inicializar aquí la lista de jugadores
        _jugadores.value = listOf(
            Jugador(id = 1, posicion = Pair(0, 0), direccion = "DERECHA"),
            Jugador(id = 2, posicion = Pair(0, 0), direccion = "DERECHA")
            // Puedes agregar más jugadores según sea necesario

        )
        // Quiero que el jugador 1 tenga los juegos 1, 2, 3 y 4 completados
        //_jugadores.value?.get(0)?.agregarJuegosCompletados(listOf("1", "2", "3", "4"))

        // Inicializar DatabaseHelper con el contexto de la aplicación
        databaseHelper = MainActivity.databaseHelper!!
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

    fun getJuegosCompletados(jugadorId: Int): List<String> {
        // Obtén el jugador correspondiente al jugadorId
        val jugador = _jugadores.value?.find { it.id == jugadorId }

        // Verifica que el jugador no sea nulo
        jugador?.let {
            // Retorna la lista de juegos completados del jugador
            return jugador.obtenerJuegosCompletados()
        }

        // Retorna una lista vacía si el jugador es nulo
        return listOf()
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

    fun guardarPartida() {
        val jugador1 = _jugadores.value?.find { it.id == 1 } ?: return
        val jugador2 = _jugadores.value?.find { it.id == 2 } ?: return
        val jugadorActivo = _jugadorActual.value ?: return

        Log.d("VistaTableroViewModel", "Guardando partida. Jugador1: $jugador1, Jugador2: $jugador2, Jugador Activo: $jugadorActivo")

        databaseHelper.insertarPartida( jugador1, jugador2, jugadorActivo)
    }

    fun cargarPartida(partidaId: Long) {
        Log.d("VistaTableroViewModel", "Cargando partida. ID: $partidaId")

        val partidaInfo = databaseHelper.obtenerPartidaPorId(partidaId)
        if (partidaInfo != null) {
            val (_, jugador1, jugador2) = partidaInfo

            Log.d("VistaTableroViewModel", "Partida cargada. Jugador1: $jugador1, Jugador2: $jugador2")

            // Actualizar jugadores en el ViewModel
            _jugadores.value = listOf(jugador1, jugador2)

            // Establecer jugador activo
            _jugadorActual.value = databaseHelper.obtenerJugadorActivoDePartida(partidaId)

            // Actualizar el tablero con las posiciones de los jugadores
            actualizarTableroConPosiciones()

            Log.d("VistaTableroViewModel", "Partida cargada. Jugadoractual: $_jugadorActual.value")

            // Aquí puedes actualizar otros elementos del ViewModel según sea necesario
        } else {
            Log.d("VistaTableroViewModel", "No se encontró la partida con ID: $partidaId")
        }
    }

    private fun actualizarTableroConPosiciones() {
        viewModelScope.launch(Dispatchers.Default) {
            val jugadores = _jugadores.value ?: return@launch
            val tablero = _tablero.value ?: return@launch

            jugadores.forEach { jugador ->
                val posicion = jugador.posicion
                if (posicion.first >= 0 && posicion.first < tablero.size &&
                    posicion.second >= 0 && posicion.second < tablero[0].size
                ) {
                    // Actualiza la posición del jugador en el ViewModel
                    actualizarPosicionJugador(jugador.id, jugador.posicion)
                }
            }
        }
    }

    fun guardarOActualizarPartida(partidaId: Long) {


        // Si hay una partida activa, actualízala; de lo contrario, guárdala como una nueva partida
        if (partidaId != -1L) {
            // Actualizar la partida existente
            actualizarPartida(partidaId)
            Log.d("VistaTableroViewModel", "Partida actualizada. ID: $partidaId")
        } else {
            // Guardar una nueva partida
            guardarPartida()
            Log.d("VistaTableroViewModel", "Nueva partida guardada.")
        }
    }

    private fun actualizarPartida(partidaId: Long) {
        val jugador1 = _jugadores.value?.find { it.id == 1 } ?: return
        val jugador2 = _jugadores.value?.find { it.id == 2 } ?: return
        val jugadorActivo = _jugadorActual.value ?: return

        Log.d("VistaTableroViewModel", "Actualizando partida. Jugador1: $jugador1, Jugador2: $jugador2, Jugador Activo: $jugadorActivo")

        databaseHelper.actualizarPartida(partidaId, jugador1, jugador2, jugadorActivo)
    }

    fun obtenerUltimoIdPartidaDesdeBDAsync(): Long {
        return databaseHelper.obtenerUltimoIdPartida()

    }

    fun borrarPartidaPorId(partidaId: Long) {
        var borradoExitoso = false
        borradoExitoso = databaseHelper.borrarPartidaPorId(partidaId)



    }

}