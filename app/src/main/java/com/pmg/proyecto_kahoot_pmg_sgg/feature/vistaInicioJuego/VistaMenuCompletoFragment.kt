package com.pmg.proyecto_kahoot_pmg_sgg.feature.vistaInicioJuego

import android.app.AlertDialog
import android.media.MediaPlayer
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.pmg.proyecto_kahoot_pmg_sgg.R
import com.pmg.proyecto_kahoot_pmg_sgg.app.MainActivity
import com.pmg.proyecto_kahoot_pmg_sgg.core.domain.model.jugador.InformacionTablero
import kotlin.properties.Delegates

class VistaMenuCompletoFragment : Fragment() {

    // Botones en la vista
    private lateinit var btnJugarLocal: Button
    private lateinit var btnJugarMultijugador: Button
    private lateinit var btnAjustes: Button
    private lateinit var btnSalir: Button

    private var mediaPlayer: MediaPlayer? = null

    /**
     * Variable para indicar si se debe reproducir música.
     */
    private var reproducirMusica by Delegates.notNull<Boolean>()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Infla el diseño de la vista del menú completo
        val viewInicio = inflater.inflate(R.layout.fragment_vista_menu_completo_view, container, false)



        mediaPlayer = MainActivity.mediaPlayer

        // Asigna referencias a los botones
        btnJugarLocal = viewInicio.findViewById(R.id.btn_JuegoLocal)
        btnJugarMultijugador = viewInicio.findViewById(R.id.btn_JuegoMultijugador)
        btnAjustes = viewInicio.findViewById(R.id.btn_Ajustes)
        btnSalir = viewInicio.findViewById(R.id.btn_Salir)

        reproducirMusica = MainActivity.reproducirMusica

        // Agrega OnClickListener al botón btnJugarLocal
        btnJugarLocal.setOnClickListener {
            // Navega al fragmento de vistaTableroView cuando se hace clic en el botón
            findNavController().navigate(VistaMenuCompletoFragmentDirections.navegarVistaTablero(InformacionTablero(jugador = 1)))
        }

        btnJugarMultijugador.setOnClickListener {
            // Muestra un Toast cuando se hace clic en el botón
            Toast.makeText(requireContext(), "Proximamente estará disponible", Toast.LENGTH_SHORT).show()
        }

        btnAjustes.setOnClickListener {
            reproducirMusica = MainActivity.reproducirMusica
            verAjustes()

        }


        btnSalir.setOnClickListener {

            // Cierra la aplicación
            mostrarDialogSalir()

        }

        // Puedes agregar lógica adicional y OnClickListener a otros botones aquí

        return viewInicio
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Agrega el OnBackPressedCallback al fragmento para evitar que se cierre la aplicación al pulsar el botón "Atrás"
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                mostrarDialogSalir()

            }
        })
    }

    /**
     * Muestra un cuadro de diálogo para confirmar la salida de la aplicación.
     */
    private fun mostrarDialogSalir() {
        // Alerta de que si quiere salir de la aplicación
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.salir_titulo))
        builder.setMessage(getString(R.string.salir_mensaje))
        builder.setPositiveButton(getString(R.string.boton_si)) { _, _ ->
            // Cierra la aplicación
            requireActivity().finish()
        }
        builder.setNegativeButton(getString(R.string.boton_no)) { _, _ ->
            // No hace nada
        }
        builder.show()
    }

    /**
     * Muestra un cuadro de diálogo con las reglas del juego.
     */
    private fun mostrarDialogReglas() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.titulo_reglas)
            .setMessage(R.string.mensaje_reglas)

        builder.setPositiveButton(R.string.boton_aceptar) { _, _ ->
            // Acciones al hacer clic en Aceptar
        }


        builder.show()
    }

    /**
     * Muestra un cuadro de diálogo con información sobre los creadores del juego.
     */
    private fun mostrarDialogCreadores() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.titulo_creadores)
            .setMessage(R.string.mensaje_creadores)

        builder.setPositiveButton(R.string.boton_aceptar) { _, _ ->
            // Acciones al hacer clic en Aceptar
        }


        builder.show()
    }

    /**
     * Inicia la reproducción del audio asociado al juego.
     */
    private fun iniciarReproduccion() {
        mediaPlayer?.start()
    }

    /**
     * Detiene la reproducción del audio asociado al juego y lo reinicia al principio.
     */
    private fun detenerReproduccion() {
        mediaPlayer?.stop()
        mediaPlayer?.prepare()
        mediaPlayer?.seekTo(0)
    }

    /**
     * Muestra un diálogo con opciones para ver las reglas, los creadores y activar/desactivar la música.
     */
    private fun verAjustes(){
        val opciones = resources.getStringArray(R.array.opciones_array)

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.opciones_dialog_titulo)
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> mostrarDialogReglas()
                    1 -> mostrarDialogCreadores()
                    2 -> {
                        if (reproducirMusica) {
                            reproducirMusica = false
                            MainActivity.reproducirMusica = reproducirMusica
                            detenerReproduccion()
                            with (MainActivity.sharedPref.edit()) {
                                putBoolean(MainActivity.MUSICA_ACTIVA_KEY, reproducirMusica)
                                apply() // Aplica los cambios
                            }
                            Toast.makeText(requireContext(), R.string.musica_desactivada, Toast.LENGTH_SHORT).show()

                        } else {
                            reproducirMusica = true
                            MainActivity.reproducirMusica = reproducirMusica
                            iniciarReproduccion()
                            with (MainActivity.sharedPref.edit()) {
                                putBoolean(MainActivity.MUSICA_ACTIVA_KEY, reproducirMusica)
                                apply() // Aplica los cambios
                            }
                            Toast.makeText(requireContext(), R.string.musica_activada, Toast.LENGTH_SHORT).show()
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


}