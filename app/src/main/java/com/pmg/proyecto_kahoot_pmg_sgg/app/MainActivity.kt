package com.pmg.proyecto_kahoot_pmg_sgg.app

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.t8_ej01_persistenciadatossqlite.DatabaseHelper
import com.pmg.proyecto_kahoot_pmg_sgg.R
import com.pmg.proyecto_kahoot_pmg_sgg.core.domain.model.NetworkUtils.NetworkUtils.isConnected

class MainActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var dialog: AlertDialog? = null
    companion object{
        var databaseHelper = null as DatabaseHelper?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        databaseHelper = DatabaseHelper(this)

        // Inicializar MediaPlayer con el archivo de música en res/raw
        mediaPlayer = MediaPlayer.create(this, R.raw.musica_fondo);

        // Reproducir la música
        mediaPlayer?.start()

        if (!isConnected(this)) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Sin conexión a Internet")
            builder.setMessage("Por favor, comprueba tu conexión a Internet y vuelve a intentarlo, si das a aceptar sin internet se cerrará la aplicación.")
            builder.setCancelable(false)

            builder.setPositiveButton("Aceptar") { _, _ ->
                if (isConnected(this)) {
                    dialog?.dismiss()
                } else {
                    finish()
                }
            }

            dialog = builder.create()
            dialog?.show()
        }
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
        mediaPlayer?.start()
    }

    fun cerrarApp() {
        finish()
    }

}