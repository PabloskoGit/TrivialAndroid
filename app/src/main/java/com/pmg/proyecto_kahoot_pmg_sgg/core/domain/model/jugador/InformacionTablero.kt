package com.pmg.proyecto_kahoot_pmg_sgg.core.domain.model.jugador

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InformacionTablero(
    val jugador: Int,
    val resultadoMemory: Boolean = false,
    val resultadoTest: Boolean = false,
    val resultadoPruebaFinal: Boolean = false,
    val resultadoRepaso: Boolean = false,
    val resutadoAhorcado: Boolean = false,
    val cambioJugador: Boolean = false
) : Parcelable
