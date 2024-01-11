package com.pmg.proyecto_kahoot_pmg_sgg.app

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.t8_ej01_persistenciadatossqlite.DatabaseHelper
import com.pmg.proyecto_kahoot_pmg_sgg.R
import com.pmg.proyecto_kahoot_pmg_sgg.app.utils.AlertaPreferencias
import com.pmg.proyecto_kahoot_pmg_sgg.app.utils.NetworkConnectivityObserver
import com.pmg.proyecto_kahoot_pmg_sgg.core.network.ConnectivityObserver
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var dialog: AlertDialog? = null

    private lateinit var connectivityObserver: ConnectivityObserver
    companion object{
        var databaseHelper = null as DatabaseHelper?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Observar la conectividad de la red si no hay conexión a internet mostrar una alerta
        checkConnection()

        // Alerta de bienvenida solo 1 vez por ejecucion de la app
        AlertaPreferencias.setDialogShown(this, false)

        // Inicializar base de datos
        databaseHelper = DatabaseHelper(this)

        // Inicializar MediaPlayer con el archivo de música en res/raw
        mediaPlayer = MediaPlayer.create(this, R.raw.musica_fondo);

        // Reproducir la música
        mediaPlayer?.start()

        // Reproducir la música en bucle
        mediaPlayer?.isLooping = true
    }

    override fun onDestroy() {
        // Liberar recursos al cerrar la actividad
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer = null
        }
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
    }

    override fun onResume() {
        super.onResume()
        checkConnection()
        mediaPlayer?.start()
    }

    fun cerrarApp() {
        finish()
    }

    private fun checkConnection() {
        // Observar la conectividad de la red si no hay conexión a internet mostrar una alerta
        connectivityObserver = NetworkConnectivityObserver(applicationContext)
        connectivityObserver.observe().onEach {

            when (it) {
                ConnectivityObserver.Status.AVAILABLE -> {
                    dialog?.dismiss()
                }
                ConnectivityObserver.Status.UNAVAILABLE -> {
                    dialog = AlertDialog.Builder(this)
                        .setTitle("No hay conexión a internet")
                        .setMessage("Por favor, conectate a internet para poder seguir jugando a nuestro juego. \n\nSi no la app se cerrará.")
                        .setCancelable(false)
                        .setPositiveButton("Salir") { _, _ ->
                            cerrarApp()
                        }
                        .show()
                }
                ConnectivityObserver.Status.LOSING -> {
                    dialog = AlertDialog.Builder(this)
                        .setTitle("Se está perdiendo la conexión a internet")
                        .setMessage("Por favor, conectate a internet para poder jugar")
                        .setCancelable(false)
                        .setPositiveButton("Salir") { _, _ ->
                            cerrarApp()
                        }
                        .show()
                }
                ConnectivityObserver.Status.LOST -> {
                    dialog = AlertDialog.Builder(this)
                        .setTitle("Se ha perdido la conexión a internet")
                        .setMessage("Por favor, conectate a internet para poder jugar")
                        .setCancelable(false)
                        .setPositiveButton("Salir") { _, _ ->
                            cerrarApp()
                        }
                        .show()
                }
            }
        }.launchIn(lifecycleScope)
    }

}