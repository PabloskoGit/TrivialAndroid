package com.pmg.proyecto_kahoot_pmg_sgg.feature.vistaMemory

import android.os.Bundle
import android.os.CountDownTimer
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.gridlayout.widget.GridLayout
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.pmg.proyecto_kahoot_pmg_sgg.R
import com.pmg.proyecto_kahoot_pmg_sgg.app.MainActivity
import com.pmg.proyecto_kahoot_pmg_sgg.core.common.ConstantesNavegacion
import com.pmg.proyecto_kahoot_pmg_sgg.core.domain.model.NetworkUtils.NetworkUtils
import com.pmg.proyecto_kahoot_pmg_sgg.core.domain.model.jugador.InformacionTablero
import kotlin.properties.Delegates

class VistaMemoryFragment : Fragment() {

    private val args: VistaMemoryFragmentArgs by navArgs()

    private val viewModel: VistaMemoryViewModel by viewModels()
    private lateinit var botones: Array<Array<Button>>
    private var primerBoton: Button? = null
    private var puntos = 0
    private var timer: CountDownTimer? = null

    private val assignedTags = mutableSetOf<String>()

    private lateinit var tx_Tiempo: TextView
    private var jugadorActivo by Delegates.notNull<Int>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewTableroMemory = inflater.inflate(R.layout.fragment_vista_memory, container, false)
        // Infla el diseño de la vista del tablero
        // Obtiene una referencia al GridLayout
        val gridLayout = viewTableroMemory.findViewById<GridLayout>(R.id.gridTableroCartas)
        // Define el número de filas y columnas para el GridLayout
        val filas = 4
        val columnas = 4
        // Establece las propiedades del GridLayout
        gridLayout.rowCount = filas
        gridLayout.columnCount = columnas

        // Llama a la función del ViewModel para crear el tablero
        viewModel.crearTablero(filas, columnas)

        return viewTableroMemory
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        jugadorActivo = args.Jugador

        // Puedes realizar acciones adicionales después de que la vista se haya creado.
        viewModel.tablero.observe(viewLifecycleOwner, Observer { tableroNuevo ->
            // Actualiza la interfaz de usuario con el nuevo tablero
            actualizarTableroUI(tableroNuevo)
            iniciarTemporizador()
            tx_Tiempo = view.findViewById(R.id.tx_Tiempo)

        })
    }

    private fun iniciarTemporizador() {
        timer = object : CountDownTimer(60000, 1000) { // 60000 milisegundos = 60 segundos
            override fun onTick(millisUntilFinished: Long) {
                // Actualizar el temporizador en la interfaz de usuario (si es necesario)
                val tiempoRestante = millisUntilFinished / 1000
                // Por ejemplo, puedes mostrar el tiempo restante en un TextView (tx_Tiempo)

                tx_Tiempo.text = "Tiempo restante: $tiempoRestante segundos"
            }

            override fun onFinish() {
                // El temporizador ha terminado, puedes realizar acciones cuando el tiempo se agota
                perderJuego()
            }
        }.start()
    }

    private fun detenerTemporizador() {
        timer?.cancel()
    }

    private fun actualizarTableroUI(tablero: Array<Array<String>>) {
        // Obtiene una referencia al GridLayout
        val gridLayout = view?.findViewById<GridLayout>(R.id.gridTableroCartas)

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
                boton.setBackgroundResource(R.drawable.background_boton_tablero_nuevo)

                // Configura los parámetros de diseño del botón
                val params = GridLayout.LayoutParams()
                params.width = 0
                params.height = 0
                params.columnSpec = GridLayout.spec(j, 1, 1f)
                params.rowSpec = GridLayout.spec(i, 1, 1f)
                boton.layoutParams = params


                // Asigna el tag al botón y lo agrega a la lista de tags asignados
                boton.tag = tablero[i][j]
                assignedTags.add(tablero[i][j])

                // Agrega un listener de clic al botón (opcional, según la lógica de tu aplicación)
                boton.setOnClickListener {
                    onBotonClicked(boton)
                }

                // Agrega el botón al GridLayout solo si tiene un texto no vacío
                gridLayout?.addView(boton)

                boton
            }
        }
    }

    private fun onBotonClicked(boton: Button) {
        // Verifica si ya hay un primer botón clicado
        if (primerBoton == null) {
            // Es el primer clic, almacena el botón
            primerBoton = boton
            // Desactiva el primer botón clicado
            primerBoton?.isEnabled = false
            // Muestra la imagen asociada al primer botón
            primerBoton?.setBackgroundResource(getBackgroundResourceFromTag(primerBoton?.tag))
        } else {
            // Es el segundo clic, compara los tags
            if (primerBoton?.tag == boton.tag) {
                // Los tags son iguales, establece el fondo según el tag
                boton.setBackgroundResource(getBackgroundResourceFromTag(boton.tag))
                // Desactiva ambos botones si los tags son iguales
                primerBoton?.isEnabled = false
                boton.isEnabled = false

                // Incrementar puntos
                puntos++

                // Verificar si se alcanzaron los 8 puntos
                if (puntos == 8) {
                    ganarJuego()
                }
            } else {
                boton.setBackgroundResource(getBackgroundResourceFromTag(boton.tag))
                boton.isEnabled = false
                Thread.sleep(500)
                // Los tags no son iguales, vuelve a activar el primer botón
                primerBoton?.isEnabled = true
                // Posterga la reversión del fondo después de 5 segundos

                // Revierte el fondo del primer botón
                primerBoton?.setBackgroundResource(R.drawable.background_boton_tablero_nuevo)
                // Revierte el fondo del segundo botón
                boton.setBackgroundResource(R.drawable.background_boton_tablero_nuevo)
                // Vuelve a habilitar ambos botones

                primerBoton?.isEnabled = true
                boton.isEnabled = true

            }

            // Restablece el primer botón a null para permitir futuros clics
            primerBoton = null
        }
    }


    private fun ganarJuego() {
        // Realizar acciones adicionales cuando se gana el juego
        detenerTemporizador()

        findNavController().previousBackStackEntry?.savedStateHandle?.apply {
            set(
                ConstantesNavegacion.infoTableroKey, InformacionTablero(
                    jugador = jugadorActivo,
                    resultadoMemory = true,
                    cambioJugador = false
                )
            )
        }

        findNavController().popBackStack(R.id.vistaTableroView, false)
    }

    private fun perderJuego() {
        // Realizar acciones adicionales cuando se pierde el juego
        // Por ejemplo, navegar hacia atrás
        detenerTemporizador()

        findNavController().previousBackStackEntry?.savedStateHandle?.apply {
            set(
                ConstantesNavegacion.infoTableroKey, InformacionTablero(
                    jugador = jugadorActivo,
                    resultadoMemory = false,
                    cambioJugador = true
                )
            )
        }

        findNavController().popBackStack(R.id.vistaTableroView, false)
    }


    // Función para obtener el recurso de fondo según el tag
    private fun getBackgroundResourceFromTag(tag: Any?): Int {
        return when (tag) {
            "1" -> R.drawable.background_boton_tablero_usado
            "2" -> R.drawable.background_boton_tablero_usado
            "3" -> R.drawable.background_boton_tablero_usado
            "4" -> R.drawable.background_boton_tablero_usado
            "5" -> R.drawable.background_boton_tablero_usado
            "6" -> R.drawable.background_boton_tablero_usado
            "7" -> R.drawable.background_boton_tablero_usado
            "8" -> R.drawable.background_boton_tablero_usado
            else -> R.drawable.background_boton_tablero_nuevo
        }
    }
}