package com.pmg.proyecto_kahoot_pmg_sgg.app

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pmg.proyecto_kahoot_pmg_sgg.core.data.persistencia.DatabaseHelper
import com.pmg.proyecto_kahoot_pmg_sgg.R
import com.pmg.proyecto_kahoot_pmg_sgg.app.utils.AlertaPreferencias
import com.pmg.proyecto_kahoot_pmg_sgg.app.utils.NetworkConnectivityObserver
import com.pmg.proyecto_kahoot_pmg_sgg.core.network.ConnectivityObserver
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainActivity : AppCompatActivity() {


    private var dialog: AlertDialog? = null

    private lateinit var connectivityObserver: ConnectivityObserver

    private lateinit var btnImgAjustes: ImageView

    companion object{
        var databaseHelper = null as DatabaseHelper?
        var mediaPlayer: MediaPlayer? = null
        lateinit var sharedPref: SharedPreferences
        var reproducirMusica = true
        const val PREFS_NAME = "MyPrefsFileMusicaActiva3"
        const val MUSICA_ACTIVA_KEY = "musicaActiva"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Observar la conectividad de la red si no hay conexión a internet mostrar una alerta
        checkConnection()

        btnImgAjustes = findViewById(R.id.btnimg_Ajustes)

        sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        reproducirMusica = sharedPref.getBoolean(MUSICA_ACTIVA_KEY, true)

        // Acceso a las preferencias compartidas



        // Alerta de bienvenida solo 1 vez por ejecucion de la app
        AlertaPreferencias.setDialogShown(this, false)

        // Inicializar base de datos
        databaseHelper = DatabaseHelper(this)

        // Inicializar MediaPlayer con el archivo de música en res/raw
        mediaPlayer = MediaPlayer.create(this, R.raw.musica_fondo);

        mediaPlayer?.start()
        if (!reproducirMusica) {

            mediaPlayer?.pause()

        }

       // Reproducir la música en bucle
        mediaPlayer?.isLooping = true

        btnImgAjustes.setOnClickListener{
            verAjustes()
        }


    }

    override fun onDestroy() {
        // Liberar recursos al cerrar la actividad
        if (reproducirMusica) {
            if (mediaPlayer != null) {
                mediaPlayer!!.release()
                mediaPlayer = null
            }
        }

        //guadarMusica()
        super.onDestroy()
    }

    override fun onPause() {

        super.onPause()
        mediaPlayer?.pause()
    }

    override fun onResume() {

        super.onResume()
        checkConnection()
        if (reproducirMusica) {
            mediaPlayer?.start()
        }

    }

    fun cerrarApp() {
        finish()
    }



    private fun checkConnection() {
        // Observar la conectividad de la red; si no hay conexión a internet, mostrar una alerta
        connectivityObserver = NetworkConnectivityObserver(applicationContext)
        connectivityObserver.observe().onEach {

            when (it) {
                ConnectivityObserver.Status.AVAILABLE -> {
                    dialog?.dismiss()
                }
                ConnectivityObserver.Status.UNAVAILABLE -> {
                    dialog = AlertDialog.Builder(this)
                        .setTitle(getString(R.string.sin_conexion_titulo))
                        .setMessage(getString(R.string.sin_conexion_mensaje))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.boton_salir)) { _, _ ->
                            cerrarApp()
                        }
                        .show()
                }
                ConnectivityObserver.Status.LOSING -> {
                    dialog = AlertDialog.Builder(this)
                        .setTitle(getString(R.string.perdiendo_conexion_titulo))
                        .setMessage(getString(R.string.perdiendo_conexion_mensaje))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.boton_salir)) { _, _ ->
                            cerrarApp()
                        }
                        .show()
                }
                ConnectivityObserver.Status.LOST -> {
                    dialog = AlertDialog.Builder(this)
                        .setTitle(getString(R.string.perdida_conexion_titulo))
                        .setMessage(getString(R.string.perdida_conexion_mensaje))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.boton_salir)) { _, _ ->
                            cerrarApp()
                        }
                        .show()
                }
            }
        }.launchIn(lifecycleScope)
    }

    private fun verAjustes(){
        val opciones = resources.getStringArray(R.array.opciones_array)

        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(R.string.opciones_dialog_titulo)
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> mostrarDialogReglas()
                    1 -> mostrarDialogCreadores()
                    2 -> {
                        if (reproducirMusica) {
                            reproducirMusica = false
                            detenerReproduccion()
                            with (sharedPref.edit()) {
                                putBoolean(MUSICA_ACTIVA_KEY, reproducirMusica)
                                apply() // Aplica los cambios
                            }
                            Toast.makeText(this, R.string.musica_desactivada, Toast.LENGTH_SHORT).show()

                        } else {
                            reproducirMusica = true
                            iniciarReproduccion()
                            with (sharedPref.edit()) {
                                putBoolean(MUSICA_ACTIVA_KEY, reproducirMusica)
                                apply() // Aplica los cambios
                            }
                            Toast.makeText(this, R.string.musica_activada, Toast.LENGTH_SHORT).show()
                        }

                    }
                }
            }

        val dialog = builder.create()
        dialog.show()

        // Establece el tamaño del diálogo y su posición
        val window = dialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setGravity(Gravity.CENTER)
    }

    private fun mostrarDialogReglas() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(R.string.titulo_reglas)
            .setMessage(R.string.mensaje_reglas)

        builder.setPositiveButton(R.string.boton_aceptar) { _, _ ->
            // Acciones al hacer clic en Aceptar
        }


        builder.show()
    }

    private fun mostrarDialogCreadores() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(R.string.titulo_creadores)
            .setMessage(R.string.mensaje_creadores)

        builder.setPositiveButton(R.string.boton_aceptar) { _, _ ->
            // Acciones al hacer clic en Aceptar
        }


        builder.show()
    }
    private fun iniciarReproduccion() {
        mediaPlayer?.start()
    }

    private fun detenerReproduccion() {
        mediaPlayer?.stop()
        mediaPlayer?.prepare()
        mediaPlayer?.seekTo(0)
    }

}