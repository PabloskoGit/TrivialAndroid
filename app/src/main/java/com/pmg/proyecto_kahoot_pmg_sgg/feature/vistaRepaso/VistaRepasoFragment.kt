package com.pmg.proyecto_kahoot_pmg_sgg.feature.vistaRepaso

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.pmg.proyecto_kahoot_pmg_sgg.R
import com.pmg.proyecto_kahoot_pmg_sgg.app.MainActivity
import com.pmg.proyecto_kahoot_pmg_sgg.core.common.ConstantesNavegacion
import com.pmg.proyecto_kahoot_pmg_sgg.core.domain.model.NetworkUtils.NetworkUtils
import com.pmg.proyecto_kahoot_pmg_sgg.core.domain.model.jugador.InformacionTablero
import kotlin.properties.Delegates

class VistaRepasoFragment : Fragment() {

    private val args: VistaRepasoFragmentArgs by navArgs()

    private val viewModel: VistaRepasoViewModel by viewModels()
    private lateinit var preguntaTextView: TextView

    private lateinit var btn0: Button
    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var btn3: Button

    private var jugadorActivo by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_vista_repaso, container, false)

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

        // Obtiene el jugador activo del argumento de navegación
        jugadorActivo = args.Jugador

        btn0.setOnClickListener{

            viewModel.comprobarRespuestaAcertada(0, btn0)
        }

        btn1.setOnClickListener{

            viewModel.comprobarRespuestaAcertada(1, btn1)
        }

        btn2.setOnClickListener{

            viewModel.comprobarRespuestaAcertada(2, btn2)
        }

        btn3.setOnClickListener{

            viewModel.comprobarRespuestaAcertada(3, btn3)
        }

        // Observa los cambios en el LiveData del ViewModel y actualiza la interfaz de usuario
        viewModel.repasoModel.observe(viewLifecycleOwner) { pregunta ->

            // Actualiza la interfaz de usuario con la nueva pregunta
            preguntaTextView.text = pregunta?.oracion

            // Actualiza los botones con las nuevas respuestas
            btn0.text = pregunta?.respuestas?.get(0).toString()
            btn1.text = pregunta?.respuestas?.get(1).toString()
            btn2.text = pregunta?.respuestas?.get(2).toString()
            btn3.text = pregunta?.respuestas?.get(3).toString()

        }

        // Observa los cambios en el LiveData de shouldNavigateBack del viewModel. Si el valor es true, navega hacia atrás
        viewModel.juegoGanado.observe(viewLifecycleOwner) { juegoGanado ->
            if (juegoGanado) {
                ganarJuego()
            }
        }

        // Observa los cambios en el LiveData de shouldShowDialog del viewModel. Si el valor es true, muestra un diálogo
        viewModel.juegoPerdido.observe(viewLifecycleOwner) { juegoPerdido ->
            if (juegoPerdido) {
                perderJuego()
            }
        }
    }

    private fun ganarJuego() {
        // Realizar acciones adicionales cuando se gana el juego

        findNavController().previousBackStackEntry?.savedStateHandle?.apply {
            set(ConstantesNavegacion.infoTableroKey, InformacionTablero(
                jugador = jugadorActivo,
                resultadoRepaso = true,
                cambioJugador = false
            ))
        }

        findNavController().popBackStack(R.id.vistaTableroView, false)
    }

    private fun perderJuego() {
        // Realizar acciones adicionales cuando se pierde el juego
        // Por ejemplo, navegar hacia atrás

        findNavController().previousBackStackEntry?.savedStateHandle?.apply {
            set(ConstantesNavegacion.infoTableroKey, InformacionTablero(
                jugador = jugadorActivo,
                resultadoRepaso = false,
                cambioJugador = true
            ))
        }

        findNavController().popBackStack(R.id.vistaTableroView, false)
    }

}