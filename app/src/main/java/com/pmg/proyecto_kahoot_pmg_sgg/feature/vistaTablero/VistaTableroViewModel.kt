package com.pmg.proyecto_kahoot_pmg_sgg.feature.vistaTablero

import android.util.Log
import androidx.lifecycle.*
import com.pmg.proyecto_kahoot_pmg_sgg.core.data.persistencia.DatabaseHelper
import com.pmg.proyecto_kahoot_pmg_sgg.app.MainActivity
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

    /**
     * LiveData que representa el tablero del juego.
     */
    private val _tablero = MutableLiveData<Array<Array<String>>>()
    val tablero: LiveData<Array<Array<String>>> get() = _tablero

    /**
     * LiveData que representa la lista de jugadores en el juego.
     */
    private val _jugadores = MutableLiveData<List<Jugador>>()
    val jugadores: LiveData<List<Jugador>> get() = _jugadores

    /**
     * LiveData que representa el jugador actual.
     */
    private val _jugadorActual = MutableLiveData<Int>()
    val jugadorActual: LiveData<Int> get() = _jugadorActual

    /**
     * Identificador del jugador activo.
     */
    var jugador: Int = 1

    /**
     * Mapa mutable que asocia el ID de un jugador con su LiveData de posición.
     * La clave es el ID del jugador, y el valor es un LiveData que contiene la posición (par de coordenadas) del jugador.
     */
    private val mapPosicionesJugadores = mutableMapOf<Int, MutableLiveData<Pair<Int, Int>>>()

    private var databaseHelper: DatabaseHelper



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


    /**
     * Crea un nuevo tablero para el juego con las dimensiones especificadas.
     *
     * @param filas Número de filas en el tablero.
     * @param columnas Número de columnas en el tablero.
     */
    fun crearTablero(filas: Int, columnas: Int) {
        _jugadorActual.postValue(jugador)

        // Utilizar un coroutine para realizar operaciones en segundo plano
        viewModelScope.launch(Dispatchers.Default) {
            // Crear un nuevo tablero bidimensional utilizando expresiones lambda
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

    /**
     * Mueve al jugador actual en el tablero según el número de casillas y direccion especificado.
     *
     * @param numeroCasillas Número de casillas que el jugador debe avanzar.
     */
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

                        // Realiza acciones basadas en la dirección actual del jugador
                        when (Direccion.valueOf(jugador.direccion)) {
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


    /**
     * Cambia al siguiente jugador en la lista de jugadores.
     * Actualiza el valor de `_jugadorActual` en el ViewModel.
     */
    fun cambiarJugador() {
        // Obtener el jugador actual del LiveData
        _jugadorActual.value?.let { jugadorActual ->

            // Calcular el nuevo jugador (circular, volviendo al primer jugador después del último)
            val nuevoJugador = (jugadorActual % (_jugadores.value?.size ?: 2)) + 1

            // Actualizar el valor de `_jugadorActual` en el ViewModel con el nuevo jugador
            _jugadorActual.postValue(nuevoJugador)

            // Actualizar la variable global 'jugador' con el nuevo jugador
            jugador = nuevoJugador
        }
    }


    /**
     * Obtiene o crea un LiveData para la posición del jugador especificado.
     *
     * @param jugadorId Identificador único del jugador.
     * @return MutableLiveData que contiene la posición actual del jugador (Par de coordenadas).
     */
    fun getPosicionJugadorLiveData(jugadorId: Int): MutableLiveData<Pair<Int, Int>> {
        if (!mapPosicionesJugadores.containsKey(jugadorId)) {
            // Si el MutableLiveData no existe, créalo y agréguelo al mapa
            mapPosicionesJugadores[jugadorId] = MutableLiveData()
        }
        // Devolver el MutableLiveData correspondiente al jugador
        return mapPosicionesJugadores[jugadorId]!!
    }


    /**
     * Actualiza la posición del jugador con el identificador especificado en el LiveData correspondiente.
     *
     * @param jugadorId Identificador único del jugador.
     * @param nuevaPosicion Nueva posición del jugador (Par de coordenadas).
     */
    private fun actualizarPosicionJugador(jugadorId: Int, nuevaPosicion: Pair<Int, Int>) {
        // Obtén el MutableLiveData del jugador y actualiza su valor
        getPosicionJugadorLiveData(jugadorId).postValue(nuevaPosicion)
    }


    /**
     * Actualiza los juegos completados y verifica la victoria del jugador con el identificador especificado.
     *
     * @param jugadorId Identificador único del jugador.
     * @param juego1 Estado del juego 1 (completado o no).
     * @param juego2 Estado del juego 2 (completado o no).
     * @param juego3 Estado del juego 3 (completado o no).
     * @param juego4 Estado del juego 4 (completado o no).
     * @param juego5 Estado del juego 5 (completado o no).
     */
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

    /**
     * Obtiene el estado de victoria del jugador actual.
     *
     * @return `true` si el jugador ha ganado, `false` de lo contrario.
     */
    fun obtenerJugadorVictoria(): Boolean {
        // Obtén el jugador actual
        val jugadorActual = _jugadores.value?.find { it.id == _jugadorActual.value }

        // Verifica que el jugador no sea nulo y retorna su estado de victoria
        return jugadorActual?.obtenerVictoria() ?: false
    }


    /**
     * Obtiene la lista de juegos completados por el jugador con el identificador especificado.
     *
     * @param jugadorId Identificador único del jugador.
     * @return Lista de juegos completados por el jugador, o una lista vacía si el jugador no existe.
     */
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


    /**
     * Obtiene las posiciones actuales de todos los jugadores.
     *
     * @return Lista de pares de coordenadas que representan las posiciones de los jugadores.
     */
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

    /**
     * Enumerado que representa las direcciones posibles en las que un jugador puede moverse.
     */
    private enum class Direccion {
        DERECHA, ABAJO, IZQUIERDA, ARRIBA
    }

    /**
     * Guarda la partida actual en la base de datos.
     * Se obtienen los jugadores y el jugador activo para almacenarlos en la base de datos.
     * Se registra un mensaje de depuración con información sobre los jugadores y el jugador activo.
     */
    fun guardarPartida() {
        // Obtener los jugadores y el jugador activo del LiveData
        val jugador1 = _jugadores.value?.find { it.id == 1 } ?: return
        val jugador2 = _jugadores.value?.find { it.id == 2 } ?: return
        val jugadorActivo = _jugadorActual.value ?: return

        // Llamar a la función de la base de datos para insertar la partida
        databaseHelper.insertarPartida( jugador1, jugador2, jugadorActivo)
    }

    /**
     * Carga una partida desde la base de datos utilizando el ID de la partida especificado.
     * Actualiza los jugadores, el jugador activo y el tablero en el ViewModel.
     * Registra mensajes de depuración para seguir el proceso de carga.
     *
     * @param partidaId ID de la partida a cargar.
     */
    fun cargarPartida(partidaId: Long) {

        val partidaInfo = databaseHelper.obtenerPartidaPorId(partidaId)
        if (partidaInfo != null) {
            val (_, jugador1, jugador2) = partidaInfo

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

    /**
     * Actualiza el tablero del juego con las posiciones actuales de los jugadores en el ViewModel.
     * Se verifica que las posiciones estén dentro de los límites del tablero antes de realizar la actualización.
     */
    private fun actualizarTableroConPosiciones() {
        viewModelScope.launch(Dispatchers.Default) {

            // Obtener la lista de jugadores y el tablero del LiveData
            val jugadores = _jugadores.value ?: return@launch
            val tablero = _tablero.value ?: return@launch

            jugadores.forEach { jugador ->
                // Obtener la posición actual del jugador
                val posicion = jugador.posicion

                // Verificar que la posición esté dentro de los límites del tablero
                if (posicion.first >= 0 && posicion.first < tablero.size &&
                    posicion.second >= 0 && posicion.second < tablero[0].size
                ) {
                    // Actualiza la posición del jugador en el ViewModel
                    actualizarPosicionJugador(jugador.id, jugador.posicion)
                }
            }
        }
    }

    /**
     * Guarda o actualiza una partida en la base de datos.
     *
     * @param partidaId ID de la partida a actualizar. Si es -1, se guarda como una nueva partida.
     */
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

    /**
     * Actualiza una partida existente en la base de datos con los jugadores y el jugador activo actuales.
     *
     * @param partidaId ID de la partida a actualizar.
     */
    private fun actualizarPartida(partidaId: Long) {

        // Obtener los jugadores y el jugador activo del LiveData
        val jugador1 = _jugadores.value?.find { it.id == 1 } ?: return
        val jugador2 = _jugadores.value?.find { it.id == 2 } ?: return
        val jugadorActivo = _jugadorActual.value ?: return

        Log.d("VistaTableroViewModel", "Actualizando partida. Jugador1: $jugador1, Jugador2: $jugador2, Jugador Activo: $jugadorActivo")

        // Llamar a la función de la base de datos para actualizar la partida
        databaseHelper.actualizarPartida(partidaId, jugador1, jugador2, jugadorActivo)
    }

    /**
     * Obtiene de forma asíncrona el último ID de partida almacenado en la base de datos.
     *
     * @return El último ID de partida, o -1 si no hay partidas registradas.
     */
    fun obtenerUltimoIdPartidaDesdeBDAsync(): Long {
        return databaseHelper.obtenerUltimoIdPartida()

    }

    /**
     * Borra una partida de la base de datos utilizando su ID.
     *
     * @param partidaId ID de la partida a borrar.
     */
    fun borrarPartidaPorId(partidaId: Long) {
        databaseHelper.borrarPartidaPorId(partidaId)
    }

}