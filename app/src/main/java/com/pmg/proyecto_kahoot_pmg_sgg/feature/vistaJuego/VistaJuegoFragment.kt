package com.pmg.proyecto_kahoot_pmg_sgg.feature.vistaJuego

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.pmg.proyecto_kahoot_pmg_sgg.R
import com.pmg.proyecto_kahoot_pmg_sgg.core.common.ConstantesNavegacion
import com.pmg.proyecto_kahoot_pmg_sgg.core.domain.model.jugador.InformacionTablero
import kotlin.properties.Delegates

// Author: Pablo Mata

class VistaJuegoFragment : Fragment() {

    private val args: VistaJuegoFragmentArgs by navArgs()

    private val viewModel: VistaJuegoViewModel by viewModels()
    private lateinit var preguntaTextView: TextView

    private lateinit var btn0: Button
    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var btn3: Button

    /**
     * Variable que almacena el número del jugador activo.
     */
    private var jugadorActivo by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_vista_juego, container, false)

        // Inicializar las vistas directamente
        preguntaTextView = view.findViewById(R.id.tv_mostrarDatos)

        btn0= view.findViewById(R.id.btnRespuesta1)
        btn1 = view.findViewById(R.id.btnRespuesta2)
        btn2 = view.findViewById(R.id.btnRespuesta3)
        btn3 = view.findViewById(R.id.btnRespuesta4)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Llama al método onCreate del ViewModel al crear la vista
        viewModel.onCreate()

        jugadorActivo = args.Jugador

        btn0.setOnClickListener{

            btn0.isEnabled = false
            btn1.isEnabled = false
            btn2.isEnabled = false
            btn3.isEnabled = false

            viewModel.comprobarRespuestaAcertada(0, btn0)
        }

        btn1.setOnClickListener{

            btn0.isEnabled = false
            btn1.isEnabled = false
            btn2.isEnabled = false
            btn3.isEnabled = false

            viewModel.comprobarRespuestaAcertada(1, btn1)
        }

        btn2.setOnClickListener{

            btn0.isEnabled = false
            btn1.isEnabled = false
            btn2.isEnabled = false
            btn3.isEnabled = false

            viewModel.comprobarRespuestaAcertada(2, btn2)
        }

        btn3.setOnClickListener{

            btn0.isEnabled = false
            btn1.isEnabled = false
            btn2.isEnabled = false
            btn3.isEnabled = false

            viewModel.comprobarRespuestaAcertada(3, btn3)
        }

        // Observa los cambios en el LiveData del ViewModel y actualiza la interfaz de usuario
        viewModel.preguntaDTOModel.observe(viewLifecycleOwner) { pregunta ->

            // Actualiza la interfaz de usuario con la nueva pregunta
            preguntaTextView.text = pregunta?.pregunta

            // Actualiza los botones con las nuevas respuestas
            btn0.text = pregunta?.respuestas?.get(0).toString()
            btn1.text = pregunta?.respuestas?.get(1).toString()
            btn2.text = pregunta?.respuestas?.get(2).toString()
            btn3.text = pregunta?.respuestas?.get(3).toString()

            btn0.isEnabled = true
            btn1.isEnabled = true
            btn2.isEnabled = true
            btn3.isEnabled = true

        }

        // Observa los cambios en el LiveData y envia al jugador a la pantalla de resultados si el juego se ha ganado
        viewModel.juegoGanado.observe(viewLifecycleOwner) { juegoGanado ->
            if (juegoGanado) {
                alertaVictoria()
            }
        }

        // Observa los cambios en el LiveData y envia al jugador a la pantalla de resultados si el juego se ha perdido
        viewModel.juegoPerdido.observe(viewLifecycleOwner) { juegoPerdido ->
            if (juegoPerdido) {
                alertaDerrota()
            }
        }
        // Agrega el OnBackPressedCallback al fragmento para evitar que se cierre la aplicación al pulsar el botón "Atrás"
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // No hace nada
            }
        })

    }

    /**
     * Función para realizar acciones adicionales cuando se gana el juego.
     */
    private fun ganarJuego() {
        // Realizar acciones adicionales cuando se gana el juego

        findNavController().previousBackStackEntry?.savedStateHandle?.apply {
            set(ConstantesNavegacion.infoTableroKey, InformacionTablero(
                jugador = jugadorActivo,
                resultadoTest = true,
                cambioJugador = false
            ))
        }

        findNavController().popBackStack(R.id.vistaTableroView, false)
    }

    /**
     * Función para realizar acciones adicionales cuando se pierde el juego.
     */
    private fun perderJuego() {
        // Realizar acciones adicionales cuando se pierde el juego
        // Por ejemplo, navegar hacia atrás

        findNavController().previousBackStackEntry?.savedStateHandle?.apply {
            set(ConstantesNavegacion.infoTableroKey, InformacionTablero(
                jugador = jugadorActivo,
                resultadoTest = false,
                cambioJugador = true
            ))
        }

        findNavController().popBackStack(R.id.vistaTableroView, false)
    }

    /**
     * Muestra una alerta de victoria con opciones adicionales.
     */
    private fun alertaVictoria() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)
        builder.setTitle(getString(R.string.alerta_victoria_titulo))
        builder.setMessage(getString(R.string.alerta_victoria_mensaje))
        builder.setPositiveButton(getString(R.string.boton_aceptar)) { _, _ ->
            ganarJuego()
        }
        builder.show()
    }

    /**
     * Muestra una alerta de derrota con opciones adicionales.
     */
    private fun alertaDerrota() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)
        builder.setTitle(getString(R.string.alerta_derrota_titulo))
        builder.setMessage(getString(R.string.alerta_derrota_mensaje))
        builder.setPositiveButton(getString(R.string.boton_aceptar)) { _, _ ->
            perderJuego()
        }
        builder.show()
    }

}