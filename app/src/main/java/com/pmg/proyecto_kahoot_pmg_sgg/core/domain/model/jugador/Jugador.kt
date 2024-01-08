package com.pmg.proyecto_kahoot_pmg_sgg.core.domain.model.jugador

class Jugador(
    val id: Int,
    var posicion: Pair<Int, Int>,
    var direccion: String
) {

    val juegosCompletados = mutableListOf<String>()
    var victoria: Boolean = false

    fun esVictoria(ganado: Boolean) {
        // Verifica si el juego ya está en la lista antes de agregarlo
        victoria = ganado
    }
    fun obtenerVictoria(): Boolean {
        // Obtiene el estado de victoria
        return victoria
    }


    fun agregarJuegoCompleto(juego: String) {
        // Verifica si el juego ya está en la lista antes de agregarlo
        if (!juegosCompletados.contains(juego)) {
            juegosCompletados.add(juego)
        }
    }

    fun haCompletadoPreguntas(vararg numerosPreguntas: Int): Boolean {
        return numerosPreguntas.all { juegosCompletados.contains(it.toString()) }
    }

    fun obtenerJuegosCompletados(): List<String> {
        return juegosCompletados
    }

    fun mostrarJuegosCompletados(): String {
        return "juegos Completados=${juegosCompletados.joinToString(", ")}"
    }

    override fun toString(): String {
        return "Jugador(id=$id, posicion=$posicion, direccion='$direccion')"
    }
}