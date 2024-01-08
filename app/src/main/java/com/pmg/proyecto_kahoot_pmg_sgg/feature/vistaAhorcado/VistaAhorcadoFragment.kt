package com.pmg.proyecto_kahoot_pmg_sgg.feature.vistaAhorcado

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.gridlayout.widget.GridLayout
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.pmg.proyecto_kahoot_pmg_sgg.R
import com.pmg.proyecto_kahoot_pmg_sgg.core.common.ConstantesNavegacion
import com.pmg.proyecto_kahoot_pmg_sgg.core.domain.model.jugador.InformacionTablero
import kotlin.properties.Delegates

// Author: Pablo Mata

class VistaAhorcadoFragment : Fragment() {

    private val args: VistaAhorcadoFragmentArgs by navArgs()

    private val viewModel: VistaAhorcadoViewModel by viewModels()
    private lateinit var palabraTextView: TextView

    private lateinit var botones: Array<Array<Button>>

    private var jugadorActivo by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_vista_ahorcado_view, container, false)

        // Obtiene una referencia a los botones
        palabraTextView = view.findViewById(R.id.tv_mostrarDatos)

        // Obtiene una referencia al GridLayout
        val gridLayout = view.findViewById<GridLayout>(R.id.gridLetras)

        // Define el número de filas y columnas para el GridLayout
        val filas = 5
        val columnas = 6

        // Establece las propiedades del GridLayout
        gridLayout.rowCount = filas
        gridLayout.columnCount = columnas

        viewModel.crearTableroLetras(filas, columnas)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.onCreate()

        jugadorActivo = args.Jugador

        viewModel.tablero.observe(viewLifecycleOwner, Observer { tableroNuevo ->
            // Actualiza la interfaz de usuario con el nuevo tablero
            actualizarTableroUI(tableroNuevo)
        })

        viewModel.palabraMostrar.observe(viewLifecycleOwner) { palabra ->

            // Actualiza la interfaz de usuario con la nueva pregunta
            palabraTextView.text = viewModel.palabraMostrar.value

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

    private fun actualizarTableroUI(tablero: Array<Array<String>>) {
        // Obtiene una referencia al GridLayout
        val gridLayout = view?.findViewById<GridLayout>(R.id.gridLetras)

        // Elimina todos los botones existentes en el GridLayout
        gridLayout?.removeAllViews()

        // Itera sobre las filas y columnas del tablero para crear botones
        botones = Array(tablero.size) { i ->
            Array(tablero[i].size) { j ->
                // Crea un nuevo botón
                val boton = Button(requireContext())

                // Establece el texto del botón según el contenido del tablero
                boton.text = tablero[i][j]

                // Establece el fondo de los botones
                boton.setBackgroundResource(R.drawable.background_boton_memory)

                // Configura los parámetros de diseño del botón
                val params = GridLayout.LayoutParams()
                params.width = 0
                params.height = 0
                params.columnSpec = GridLayout.spec(j, 1, 1f)
                params.rowSpec = GridLayout.spec(i, 1, 1f)
                params.setMargins(10, 10, 10, 10)
                boton.layoutParams = params

                // Agrega un listener de clic al botón (opcional, según la lógica de tu aplicación)
                boton.setOnClickListener {

                    if (viewModel.comprobarLetraAcertada(boton.text.toString().lowercase())){
                        boton.setBackgroundResource(R.drawable.background_boton_acierto)
                    } else {
                        boton.setBackgroundResource(R.drawable.background_boton_error)
                    }
                }

                // Agrega el botón al GridLayout solo si tiene un texto no vacío
                if (boton.text != "") {
                    gridLayout?.addView(boton)
                }

                boton
            }
        }
    }

    private fun ganarJuego() {
        // Realizar acciones adicionales cuando se gana el juego

        findNavController().previousBackStackEntry?.savedStateHandle?.apply {
            set(
                ConstantesNavegacion.infoTableroKey, InformacionTablero(
                jugador = jugadorActivo,
                    resutadoAhorcado = true,
                cambioJugador = false
            )
            )
        }

        findNavController().popBackStack(R.id.vistaTableroView, false)
    }

    private fun perderJuego() {
        // Realizar acciones adicionales cuando se pierde el juego
        // Por ejemplo, navegar hacia atrás

        findNavController().previousBackStackEntry?.savedStateHandle?.apply {
            set(
                ConstantesNavegacion.infoTableroKey, InformacionTablero(
                jugador = jugadorActivo,
                    resutadoAhorcado = false,
                cambioJugador = true
            )
            )
        }

        findNavController().popBackStack(R.id.vistaTableroView, false)
    }


}