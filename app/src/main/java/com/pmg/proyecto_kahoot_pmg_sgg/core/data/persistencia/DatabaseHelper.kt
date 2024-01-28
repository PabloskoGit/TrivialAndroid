package com.pmg.proyecto_kahoot_pmg_sgg.core.data.persistencia

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.pmg.proyecto_kahoot_pmg_sgg.core.domain.model.partida.Partida
import com.pmg.proyecto_kahoot_pmg_sgg.core.domain.model.jugador.Jugador

// Clase DatabaseHelper que extiende SQLiteOpenHelper para manejar la base de datos de la aplicación.
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // Bloque companion object para definir constantes que serán usadas en toda la clase.
    // Son como los valores estáticos en Java
    companion object {
        private const val DATABASE_NAME = "JuegosDatabase"
        private const val DATABASE_VERSION = 7
        private const val TABLE_PARTIDAS = "partidas"
        private const val KEY_ID = "id"
        private const val KEY_JUGADOR1_ID = "jugador1_id"
        private const val KEY_POSICION_FILA_JUGADOR1 = "posicion_fila_jugador1"
        private const val KEY_POSICION_COLUMNA_JUGADOR1 = "posicion_columna_jugador1"
        private const val KEY_DIRECCION_JUGADOR1 = "direccion_jugador1"
        private const val KEY_JUEGOS_COMPLETADOS_JUGADOR1 = "juegos_completados_jugador1"
        private const val KEY_VICTORIA_JUGADOR1 = "victoria_jugador1"
        private const val KEY_JUGADOR2_ID = "jugador2_id"
        private const val KEY_POSICION_FILA_JUGADOR2 = "posicion_fila_jugador2"
        private const val KEY_POSICION_COLUMNA_JUGADOR2 = "posicion_columna_jugador2"
        private const val KEY_DIRECCION_JUGADOR2 = "direccion_jugador2"
        private const val KEY_JUEGOS_COMPLETADOS_JUGADOR2 = "juegos_completados_jugador2"
        private const val KEY_VICTORIA_JUGADOR2 = "victoria_jugador2"
        private const val KEY_JUGADOR_ACTIVO = "jugador_activo"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createPartidasTable = ("CREATE TABLE " + TABLE_PARTIDAS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_JUGADOR1_ID + " INTEGER,"
                + KEY_POSICION_FILA_JUGADOR1 + " INTEGER,"
                + KEY_POSICION_COLUMNA_JUGADOR1 + " INTEGER,"
                + KEY_DIRECCION_JUGADOR1 + " TEXT,"
                + KEY_JUEGOS_COMPLETADOS_JUGADOR1 + " TEXT,"
                + KEY_VICTORIA_JUGADOR1 + " INTEGER,"
                + KEY_JUGADOR2_ID + " INTEGER,"
                + KEY_POSICION_FILA_JUGADOR2 + " INTEGER,"
                + KEY_POSICION_COLUMNA_JUGADOR2 + " INTEGER,"
                + KEY_DIRECCION_JUGADOR2 + " TEXT,"
                + KEY_JUEGOS_COMPLETADOS_JUGADOR2 + " TEXT,"
                + KEY_VICTORIA_JUGADOR2 + " INTEGER,"
                + KEY_JUGADOR_ACTIVO + " INTEGER" + ")")
        db.execSQL(createPartidasTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PARTIDAS")
        onCreate(db)
    }

    fun insertarPartida( jugador1: Jugador, jugador2: Jugador, jugadorActivo: Int) {
        val db = writableDatabase

        val values = ContentValues().apply {
            put(KEY_JUGADOR1_ID, jugador1.id)
            put(KEY_POSICION_FILA_JUGADOR1, jugador1.posicion.first)
            put(KEY_POSICION_COLUMNA_JUGADOR1, jugador1.posicion.second)
            put(KEY_DIRECCION_JUGADOR1, jugador1.direccion)
            put(KEY_JUEGOS_COMPLETADOS_JUGADOR1, jugador1.mostrarJuegosCompletados())
            put(KEY_VICTORIA_JUGADOR1, if (jugador1.obtenerVictoria()) 1 else 0)
            put(KEY_JUGADOR2_ID, jugador2.id)
            put(KEY_POSICION_FILA_JUGADOR2, jugador2.posicion.first)
            put(KEY_POSICION_COLUMNA_JUGADOR2, jugador2.posicion.second)
            put(KEY_DIRECCION_JUGADOR2, jugador2.direccion)
            put(KEY_JUEGOS_COMPLETADOS_JUGADOR2, jugador2.mostrarJuegosCompletados())
            put(KEY_VICTORIA_JUGADOR2, if (jugador2.obtenerVictoria()) 1 else 0)
            put(KEY_JUGADOR_ACTIVO, jugadorActivo)
        }

        db.insert(TABLE_PARTIDAS, null, values)
        db.close()
    }

    fun obtenerPartidaPorId(partidaId: Long): Triple<Long, Jugador, Jugador>? {
        val db = readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_PARTIDAS WHERE $KEY_ID = ?"
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, arrayOf(partidaId.toString()))
        } catch (e: SQLiteException) {
            // Manejar la excepción según tus necesidades
            Log.e("DatabaseHelper", "Error al obtener partida por ID: ${e.message}")
            db.close()
            return null
        }

        var partidaInfo: Triple<Long, Jugador, Jugador>? = null

        if (cursor.moveToFirst()) {
            val jugador1 = crearJugadorDesdeCursor(cursor, true)
            val jugador2 = crearJugadorDesdeCursor(cursor, false)

            // Obtén el índice de la columna KEY_JUGADOR_ACTIVO
//            val jugadorActivoColumnIndex = cursor.getColumnIndex(KEY_JUGADOR_ACTIVO)

//            // Verifica si la columna KEY_JUGADOR_ACTIVO está presente en el cursor
//            val jugadorActivo = if (jugadorActivoColumnIndex != -1) {
//                cursor.getInt(jugadorActivoColumnIndex)
//            } else {
//                // Manejar el caso en el que la columna no está presente
//                // Puedes asignar un valor predeterminado o lanzar una excepción según tus necesidades
//                -1 // O cualquier otro valor predeterminado
//            }

            // Crea la tripleta con la información de la partida
            partidaInfo = Triple(partidaId, jugador1, jugador2)
        }

        cursor.close()
        db.close()
        return partidaInfo
    }

    private fun crearJugadorDesdeCursor(cursor: Cursor, esJugador1: Boolean): Jugador {
        val jugadorIdColumn = if (esJugador1) KEY_JUGADOR1_ID else KEY_JUGADOR2_ID
        val posicionFilaColumn = if (esJugador1) KEY_POSICION_FILA_JUGADOR1 else KEY_POSICION_FILA_JUGADOR2
        val posicionColumnaColumn = if (esJugador1) KEY_POSICION_COLUMNA_JUGADOR1 else KEY_POSICION_COLUMNA_JUGADOR2
        val direccionColumn = if (esJugador1) KEY_DIRECCION_JUGADOR1 else KEY_DIRECCION_JUGADOR2
        val juegosCompletadosColumn = if (esJugador1) KEY_JUEGOS_COMPLETADOS_JUGADOR1 else KEY_JUEGOS_COMPLETADOS_JUGADOR2
        val victoriaColumn = if (esJugador1) KEY_VICTORIA_JUGADOR1 else KEY_VICTORIA_JUGADOR2

        // Verificar si las columnas están presentes en el cursor
        val jugadorIdIndex = cursor.getColumnIndex(jugadorIdColumn)
        val posicionFilaIndex = cursor.getColumnIndex(posicionFilaColumn)
        val posicionColumnaIndex = cursor.getColumnIndex(posicionColumnaColumn)
        val direccionIndex = cursor.getColumnIndex(direccionColumn)
        val juegosCompletadosIndex = cursor.getColumnIndex(juegosCompletadosColumn)
        val victoriaIndex = cursor.getColumnIndex(victoriaColumn)

        if (jugadorIdIndex == -1 || posicionFilaIndex == -1 || posicionColumnaIndex == -1 ||
            direccionIndex == -1 || juegosCompletadosIndex == -1 || victoriaIndex == -1) {
            // Manejar el caso en el que alguna de las columnas no está presente
            // Puedes asignar valores predeterminados o lanzar una excepción según tus necesidades
            return Jugador(-1, Pair(0, 0), "DEFAULT_DIRECTION") // O cualquier otro valor predeterminado
        }

        // Obtén los valores del cursor
        val id = cursor.getInt(jugadorIdIndex)
        val fila = cursor.getInt(posicionFilaIndex)
        val columna = cursor.getInt(posicionColumnaIndex)
        val direccion = cursor.getString(direccionIndex)
        val juegosCompletados = cursor.getString(juegosCompletadosIndex).split(", ").toList()
        val victoria = cursor.getInt(victoriaIndex) == 1

        // Crea y devuelve un objeto Jugador
        val jugador = Jugador(id, Pair(fila, columna), direccion)
        jugador.agregarJuegosCompletados(juegosCompletados)
        jugador.esVictoria(victoria)

        return jugador
    }

    fun obtenerJugadorActivoDePartida(partidaId: Long): Int {
        val db = readableDatabase
        var jugadorActivo = -1 // Valor predeterminado

        val selectQuery = "SELECT $KEY_JUGADOR_ACTIVO FROM $TABLE_PARTIDAS WHERE $KEY_ID = $partidaId"
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            // Manejar la excepción según tus necesidades
            Log.e("DatabaseHelper", "Error al obtener el jugador activo: ${e.message}")
            db.close()
            return jugadorActivo
        }

        if (cursor != null && cursor.moveToFirst()) {
            val jugadorActivoIndex = cursor.getColumnIndex(KEY_JUGADOR_ACTIVO)

            // Verificar si la columna existe en el cursor
            if (jugadorActivoIndex != -1) {
                jugadorActivo = cursor.getInt(jugadorActivoIndex)
            } else {
                // La columna no existe en el cursor, manejar este caso según tus necesidades
                // Puedes asignar un valor predeterminado, lanzar una excepción, etc.
                Log.e("DatabaseHelper", "La columna KEY_JUGADOR_ACTIVO no se encontró en el cursor.")
            }
        }

        cursor?.close()
        db.close()
        return jugadorActivo
    }


    fun obtenerTodasLasPartidas(): ArrayList<Partida> {
        val partidasList = ArrayList<Partida>()
        val db = readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_PARTIDAS"
        val cursor: Cursor

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            // Manejar la excepción según tus necesidades
            Log.e("DatabaseHelper", "Error al obtener todas las partidas: ${e.message}")
            db.close()
            return partidasList
        }

        if (cursor.moveToFirst()) {
            do {
                val partidaIdColumnIndex = cursor.getColumnIndex(KEY_ID)
                if (partidaIdColumnIndex != -1) {
                    val partidaId = cursor.getLong(partidaIdColumnIndex)

                    val juegosCompletadosJugador1ColumnIndex = cursor.getColumnIndex(
                        KEY_JUEGOS_COMPLETADOS_JUGADOR1
                    )
                    val juegosCompletadosJugador2ColumnIndex = cursor.getColumnIndex(
                        KEY_JUEGOS_COMPLETADOS_JUGADOR2
                    )

                    // Verificar si las columnas existen en el cursor antes de intentar obtener su valor
                    if (juegosCompletadosJugador1ColumnIndex != -1 && juegosCompletadosJugador2ColumnIndex != -1) {
                        val juegosCompletadosJugador1 = cursor.getString(juegosCompletadosJugador1ColumnIndex)
                        val juegosCompletadosJugador2 = cursor.getString(juegosCompletadosJugador2ColumnIndex)

                        val juegosCompletadosJugador1List = juegosCompletadosJugador1?.split(", ")
                        Log.d("DatabaseHelper", "Lista después de split. juegosCompletadosJugador1List: $juegosCompletadosJugador1List")
                        val juegosCompletadosJugador2List = juegosCompletadosJugador2?.split(", ")
                        Log.d("DatabaseHelper", "Lista después de split. juegosCompletadosJugador1List: $juegosCompletadosJugador2List")

                        // Contar la cantidad de "true" en la lista de juegos completados para cada jugador
                        val cantidadTrueJugador1 = juegosCompletadosJugador1?.split(", ")?.map { it.toIntOrNull() }?.count { it != null && it != 0 } ?: 0
                        val cantidadTrueJugador2 = juegosCompletadosJugador2?.split(", ")?.map { it.toIntOrNull() }?.count { it != null && it != 0 } ?: 0

                        Log.d("DatabaseHelper", "Conteo Jugadores. cantidadTrueJugador1: $cantidadTrueJugador1, cantidadTrueJugador2: $cantidadTrueJugador2")

                        val partida = Partida(partidaId.toInt(), cantidadTrueJugador1, cantidadTrueJugador2)
                        partidasList.add(partida)
                    }
                }
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return partidasList
    }

    fun actualizarPartida(partidaId: Long, jugador1: Jugador, jugador2: Jugador, jugadorActivo: Int) {
        val db = writableDatabase

        val values = ContentValues().apply {
            put(KEY_JUGADOR1_ID, jugador1.id)
            put(KEY_POSICION_FILA_JUGADOR1, jugador1.posicion.first)
            put(KEY_POSICION_COLUMNA_JUGADOR1, jugador1.posicion.second)
            put(KEY_DIRECCION_JUGADOR1, jugador1.direccion)
            put(KEY_JUEGOS_COMPLETADOS_JUGADOR1, jugador1.mostrarJuegosCompletados())
            put(KEY_VICTORIA_JUGADOR1, if (jugador1.obtenerVictoria()) 1 else 0)
            put(KEY_JUGADOR2_ID, jugador2.id)
            put(KEY_POSICION_FILA_JUGADOR2, jugador2.posicion.first)
            put(KEY_POSICION_COLUMNA_JUGADOR2, jugador2.posicion.second)
            put(KEY_DIRECCION_JUGADOR2, jugador2.direccion)
            put(KEY_JUEGOS_COMPLETADOS_JUGADOR2, jugador2.mostrarJuegosCompletados())
            put(KEY_VICTORIA_JUGADOR2, if (jugador2.obtenerVictoria()) 1 else 0)
            put(KEY_JUGADOR_ACTIVO, jugadorActivo)
        }

        // Utiliza el método update para actualizar la partida existente
        db.update(TABLE_PARTIDAS, values, "$KEY_ID=?", arrayOf(partidaId.toString()))

        db.close()
    }

    fun obtenerUltimoIdPartida(): Long {
        val db = readableDatabase
        val selectQuery = "SELECT MAX($KEY_ID) FROM $TABLE_PARTIDAS"
        val cursor: Cursor

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            // Manejar la excepción según tus necesidades
            Log.e("DatabaseHelper", "Error al obtener el último ID de partida: ${e.message}")
            db.close()
            return -1
        }

        var ultimoIdPartida: Long = -1

        if (cursor.moveToFirst()) {
            val ultimoIdIndex = cursor.getColumnIndex("MAX($KEY_ID)")
            if (ultimoIdIndex != -1) {
                ultimoIdPartida = cursor.getLong(ultimoIdIndex)
            } else {
                // La columna no existe en el cursor, manejar este caso según tus necesidades
                // Puedes asignar un valor predeterminado, lanzar una excepción, etc.
                Log.e("DatabaseHelper", "La columna MAX($KEY_ID) no se encontró en el cursor.")
            }
        }

        cursor.close()
        db.close()
        return ultimoIdPartida
    }

    fun borrarPartidaPorId(partidaId: Long): Boolean {
        val db = writableDatabase

        return try {
            // Utiliza el método delete para borrar la partida por su ID
            db.delete(TABLE_PARTIDAS, "$KEY_ID=?", arrayOf(partidaId.toString()))
            db.close()
            true
        } catch (e: SQLiteException) {
            // Manejar la excepción según tus necesidades
            Log.e("DatabaseHelper", "Error al borrar la partida por ID: ${e.message}")
            db.close()
            false
        }
    }

}


