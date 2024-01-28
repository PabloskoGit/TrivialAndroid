package com.pmg.proyecto_kahoot_pmg_sgg.feature.vistaMemory

import android.os.Bundle
import android.os.CountDownTimer
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.gridlayout.widget.GridLayout
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.pmg.proyecto_kahoot_pmg_sgg.R
import com.pmg.proyecto_kahoot_pmg_sgg.core.common.ConstantesNavegacion
import com.pmg.proyecto_kahoot_pmg_sgg.core.domain.model.jugador.InformacionTablero
import kotlin.properties.Delegates
class VistaMemoryFragment : Fragment() {

    private val args: VistaMemoryFragmentArgs by navArgs()

    private val viewModel: VistaMemoryViewModel by viewModels()

    /**
     * Matriz de botones en el tablero del juego Memory.
     */
    private lateinit var botones: Array<Array<Button>>

    /**
     * Primer botón clicado por el jugador en el juego Memory.
     */
    private var primerBoton: Button? = null

    /**
     * Puntuación del jugador en el juego Memory.
     */
    private var puntos = 0

    /**
     * Temporizador utilizado en el juego Memory.
     */
    private var timer: CountDownTimer? = null

    /**
     * Conjunto que almacena los tags asignados a los botones del juego Memory.
     */
    private val assignedTags = mutableSetOf<String>()

    /**
     * TextView utilizado para mostrar el tiempo restante en el juego Memory.
     */
    private lateinit var tx_Tiempo: TextView

    /**
     * Identificador del jugador activo en el juego Memory.
     */
    private var jugadorActivo by Delegates.notNull<Int>()


    /**
     * Contador de tags asignados a los botones del juego Memory.
     */
    private var contadorTags = 0

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
        viewModel.tablero.observe(viewLifecycleOwner) { tableroNuevo ->
            // Actualiza la interfaz de usuario con el nuevo tablero
            actualizarTableroUI(tableroNuevo)
            iniciarTemporizador()
            tx_Tiempo = view.findViewById(R.id.tx_Tiempo)

        }

        // Agrega el OnBackPressedCallback al fragmento para evitar que se cierre la aplicación al pulsar el botón "Atrás"
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // No hace nada
            }
        })
    }

    /**
     * Inicia un temporizador con un conteo descendente de 40 segundos y realiza acciones
     * periódicas cada segundo. Actualiza la interfaz de usuario con el tiempo restante.
     * Cuando el temporizador llega a cero, muestra una alerta de derrota.
     */
    private fun iniciarTemporizador() {
        timer = object : CountDownTimer(40000, 1000) { // 60000 milisegundos = 60 segundos
            override fun onTick(millisUntilFinished: Long) {
                // Actualizar el temporizador en la interfaz de usuario (si es necesario)
                val tiempoRestante = millisUntilFinished / 1000
                // Por ejemplo, puedes mostrar el tiempo restante en un TextView (tx_Tiempo)

                tx_Tiempo.text = "Tiempo restante: $tiempoRestante segundos"
            }

            override fun onFinish() {
                // El temporizador ha terminado, puedes realizar acciones cuando el tiempo se agota
                alertaDerrota()
            }
        }.start()
    }

    /**
     * Detiene el temporizador en curso. Cancela la cuenta regresiva y detiene cualquier
     * acción asociada al temporizador.
     */
    private fun detenerTemporizador() {
        timer?.cancel()
    }

    /**
     * Actualiza la interfaz de usuario con el nuevo estado del tablero.
     *
     * @param tablero La matriz que representa el estado actualizado del tablero.
     */
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
                boton.tag = tablero[i][j]

                contadorTags ++

                if (contadorTags <= 8){
                    boton.text = tablero[i][j] + "text"
                    //boton.setBackgroundResource()
                } else {
                    boton.text = tablero[i][j] + "img"
                }
                // Obtén el color transparente predefinido
                val colorTransparente = ContextCompat.getColor(requireContext(), android.R.color.transparent)

                // Establece el color del texto del botón como transparente
                boton.setTextColor(colorTransparente)


                // Establece el fondo de los botones
                //boton.setBackgroundResource(R.drawable.background_boton_tablero_nuevo)

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

                // Muestra la imagen asociada al primer botón
                boton.setBackgroundResource(getBackgroundResourceFromTag(boton.text))

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


    /**
     * Función llamada cuando se hace clic en un botón del tablero de memoria.
     *
     * @param boton El botón que se ha clicado.
     */
    private fun onBotonClicked(boton: Button) {
        // Verifica si ya hay un primer botón clicado
        if (primerBoton == null) {
            // Es el primer clic, almacena el botón
            primerBoton = boton
            // Desactiva el primer botón clicado
            primerBoton?.isEnabled = false
            // Muestra la imagen asociada al primer botón
            //primerBoton?.setBackgroundResource(getBackgroundResourceFromTag(primerBoton?.text))
        } else {
            // Es el segundo clic, compara los tags solo si ambos botones pertenecen a la misma mitad
            if (esMismaMitad(primerBoton, boton)) {

                // Es el segundo clic, compara los tags
                if (primerBoton?.tag == boton.tag) {
                    // Los tags son iguales, establece el fondo según el tag
                    boton.setBackgroundResource(getBackgroundResourceFromTag(boton.text))
                    // Desactiva ambos botones si los tags son iguales
                    primerBoton?.isEnabled = false
                    boton.isEnabled = false

                    primerBoton?.visibility = View.INVISIBLE
                    boton.visibility = View.INVISIBLE

                    // Incrementar puntos
                    puntos++

                    // Verificar si se alcanzaron los 8 puntos
                    if (puntos == 8) {
                        alertaVictoria()
                    }
                } else {
                    boton.setBackgroundResource(getBackgroundResourceFromTag(boton.text))
                    boton.isEnabled = false
                    //Thread.sleep(500)
                    // Los tags no son iguales, vuelve a activar el primer botón
                    primerBoton?.isEnabled = true
                    // Posterga la reversión del fondo después de 5 segundos

                    // Revierte el fondo del primer botón
                    //primerBoton?.setBackgroundResource(R.drawable.background_boton_tablero_nuevo)
                    // Revierte el fondo del segundo botón
                    //boton.setBackgroundResource(R.drawable.background_boton_tablero_nuevo)
                    // Vuelve a habilitar ambos botones

                    primerBoton?.isEnabled = true
                    boton.isEnabled = true
                }

                // Restablece el primer botón a null para permitir futuros clics
                primerBoton = null
            }
        }
    }

    /**
     * Determina si dos botones pertenecen a la misma mitad del tablero de memoria.
     *
     * @param boton1 El primer botón.
     * @param boton2 El segundo botón.
     * @return `true` si ambos botones pertenecen a la misma mitad, `false` en caso contrario.
     */
    private fun esMismaMitad(boton1: Button?, boton2: Button?): Boolean {
        return (boton1 != null && boton2 != null &&
                (boton1.top <= (boton1.height * 2) || boton2.top <= (boton2.height * 2)))
    }


    /**
     * Ejecuta acciones adicionales cuando el jugador gana el juego de memoria.
     * Detiene el temporizador, guarda la información del tablero y navega hacia atrás en el flujo de navegación.
     */
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

    /**
     * Ejecuta acciones adicionales cuando el jugador pierde el juego de memoria.
     * Detiene el temporizador, guarda la información del tablero y navega hacia atrás en el flujo de navegación.
     */
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

    /**
     * Obtiene el recurso de fondo según el tag proporcionado.
     *
     * @param text Valor del tag que determina el recurso de fondo.
     * @return ID del recurso de fondo correspondiente al tag.
     */
    private fun getBackgroundResourceFromTag(text: Any?): Int {
        return when (text) {
            "1text" -> R.drawable.memory_basket_text
            "2text" -> R.drawable.memory_boxeo_text
            "3text" -> R.drawable.memory_esgrima_text
            "4text" -> R.drawable.memory_futbol_text
            "5text" -> R.drawable.memory_golf_text
            "6text" -> R.drawable.memory_tenis_text
            "7text" -> R.drawable.memory_volley_text
            "8text" -> R.drawable.memory_natacion_text
            "1img" -> R.drawable.memory_basket
            "2img" -> R.drawable.memory_boxeo
            "3img" -> R.drawable.memory_esgrima
            "4img" -> R.drawable.memory_futbol
            "5img" -> R.drawable.memory_golf
            "6img" -> R.drawable.memory_tenis
            "7img" -> R.drawable.memory_volley
            "8img" -> R.drawable.memory_natacion
            else -> R.drawable.background_boton_tablero_nuevo
        }
    }

    /**
     * Muestra un cuadro de diálogo de victoria al completar el juego.
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
     * Muestra un cuadro de diálogo de derrota al perder el juego.
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