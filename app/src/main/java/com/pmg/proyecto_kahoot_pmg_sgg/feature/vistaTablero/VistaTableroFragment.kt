package com.pmg.proyecto_kahoot_pmg_sgg.feature.vistaTablero

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.gridlayout.widget.GridLayout
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.pmg.proyecto_kahoot_pmg_sgg.R
import com.pmg.proyecto_kahoot_pmg_sgg.app.MainActivity
import com.pmg.proyecto_kahoot_pmg_sgg.app.utils.AlertaPreferencias
import com.pmg.proyecto_kahoot_pmg_sgg.core.common.ConstantesNavegacion
import com.pmg.proyecto_kahoot_pmg_sgg.core.domain.model.NetworkUtils.NetworkUtils
import com.pmg.proyecto_kahoot_pmg_sgg.core.domain.model.jugador.InformacionTablero
import com.pmg.proyecto_kahoot_pmg_sgg.feature.vistaSeleccionPartida.VistaSeleccionPartida

/**
 * Fragmento que representa la vista del tablero del juego.
 */
class VistaTableroFragment : Fragment() {

    private val args: VistaTableroFragmentArgs by navArgs()

    private val viewModel: VistaTableroViewModel by viewModels()
    private lateinit var btnLanzarDado: Button
    private lateinit var txtJugadorActivo: TextView
    private lateinit var txtPuntosJugador: TextView

    private lateinit var btnJuego1: Button
    private lateinit var btnJuego2: Button
    private lateinit var btnJuego3: Button
    private lateinit var btnJuego4: Button

    private lateinit var btnGuardarPartida: Button
    private lateinit var btnCargarPartida: Button
    private lateinit var btnGuardarPartidaExistente: Button

    private lateinit var botones: Array<Array<Button>>

    private var numMinijuego: Int = 0
    private var jugar: Boolean = false
    private var jugador: Int = 0
    private var ultimaPosicionJugador: Pair<Int, Int> = Pair(0, 0)

    private var partidaCargada = 0

    private var dialog: AlertDialog? = null


    private val startForResultCargarPartida = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        // Este bloque de código se ejecutará cuando VistaSeleccionPartida envíe un resultado de vuelta.
        // Si el resultado es correcto
        if (result.resultCode == Activity.RESULT_OK) {
            // Obtenemos el dato "selectedPartidaId" del Intent que ha vuelto como un Int
            val partidaCargadaDevuelta = result.data?.getIntExtra("selectedPartidaId", 0)
            // Establecemos la variable con la partida seleccionada.
            partidaCargada = partidaCargadaDevuelta ?: 0
            if (partidaCargada > 0) {

                val partidaCargadaLong: Long = partidaCargada.toLong()
                viewModel.cargarPartida(partidaCargadaLong)

            }
        }
    }

    private val startForResultGuardarPartidaExistente = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        // Este bloque de código se ejecutará cuando VistaSeleccionPartida envíe un resultado de vuelta.
        // Si el resultado es correcto
        if (result.resultCode == Activity.RESULT_OK) {
            // Obtenemos el dato "selectedPartidaId" del Intent que ha vuelto como un Int
            val partidaCargadaDevuelta = result.data?.getIntExtra("selectedPartidaId", 0)
            // Establecemos la variable con la partida seleccionada.
            partidaCargada = partidaCargadaDevuelta ?: 0
            if (partidaCargada > 0) {

                val partidaCargadaLong: Long = partidaCargada.toLong()
                viewModel.guardarOActualizarPartida(partidaCargadaLong)

            }
        }
    }


    /**
     * Método llamado al crear la vista del fragmento.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla el diseño de la vista del tablero
        val viewTablero = inflater.inflate(R.layout.fragment_vista_tablero_view, container, false)

        // Obtiene referencias a las vistas necesarias
        btnLanzarDado = viewTablero.findViewById(R.id.btn_LanzarDado)
        txtJugadorActivo = viewTablero.findViewById(R.id.txt_UsuarioActivo)
        txtPuntosJugador = viewTablero.findViewById(R.id.txt_PuntosUsuario)

        btnJuego1 = viewTablero.findViewById(R.id.btnJuego1)
        btnJuego2 = viewTablero.findViewById(R.id.btnJuego2)
        btnJuego3 = viewTablero.findViewById(R.id.btnJuego3)
        btnJuego4 = viewTablero.findViewById(R.id.btnJuego4)

        btnGuardarPartida = viewTablero.findViewById(R.id.btn_GuardarPartida)
        btnCargarPartida = viewTablero.findViewById(R.id.btn_CargarPartida)
        btnGuardarPartidaExistente = viewTablero.findViewById(R.id.btn_GuardarPartidaExistente)
        // Obtiene una referencia al GridLayout
        val gridLayout = viewTablero.findViewById<GridLayout>(R.id.gridTablero)

        // Define el número de filas y columnas para el GridLayout
        val filas = 7
        val columnas = 7

        // Establece las propiedades del GridLayout
        gridLayout.rowCount = filas
        gridLayout.columnCount = columnas

        // Llama a la función del ViewModel para crear el tablero
        viewModel.crearTablero(filas, columnas)

        return viewTablero
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!NetworkUtils.isConnected(requireContext())) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Sin conexión a Internet")
            builder.setMessage("Por favor, comprueba tu conexión a Internet y vuelve a intentarlo, si das a aceptar sin internet se cerrará la aplicación.")
            builder.setCancelable(false)

            builder.setPositiveButton("Aceptar") { _, _ ->
                if (NetworkUtils.isConnected(requireContext())) {
                    dialog?.dismiss()
                } else {
                    (context as? MainActivity)?.cerrarApp()
                }
            }

            dialog = builder.create()
            dialog?.show()
        }

        val infoTablero = args.informacionTablero

        if (!AlertaPreferencias.wasDialogShown(requireContext())) {
            alertaBienvenida()
            AlertaPreferencias.setDialogShown(requireContext(), true)
        }

        // Observa los cambios en el LiveData del tablero
        viewModel.tablero.observe(viewLifecycleOwner, Observer { tableroNuevo ->
            // Actualiza la interfaz de usuario con el nuevo tablero
            actualizarTableroUI(tableroNuevo)

            // Obtiene todas las posiciones de los jugadores
            val posicionesJugadores = viewModel.obtenerPosicionesTodosJugadores()

            // Utiliza posicionesJugadores para actualizar la interfaz de usuario
            // Implementa lógica de actualización según tus necesidades
            posicionesJugadores.forEach { posicion ->
                // Actualiza la posición del jugador en la interfaz de usuario
                actualizarPosicionJugadorUI(posicion)
            }

            txtJugadorActivo.text = "Jugador: ${viewModel.jugadorActual.value}"

            viewModel.actualizarPuntosJugador(
                jugadorId = infoTablero.jugador,
                juego1 = infoTablero.resultadoRepaso,
                juego2 = infoTablero.resultadoMemory,
                juego3 = infoTablero.resultadoTest,
                juego4 = infoTablero.resutadoAhorcado,
                juego5 = infoTablero.resultadoPruebaFinal
            )

            //txtPuntosJugador.text = "juegos Completados=${viewModel.actualizarTextoPuntosJugador(jugador)}"
            txtPuntosJugador.text = "Minijuegos Completados"

            val juegosCompletados = viewModel.getJuegosCompletados(infoTablero.jugador)
            val botones = listOf(btnJuego1, btnJuego2, btnJuego3, btnJuego4)

            for (i in botones.indices) {
                when {
                    i < juegosCompletados.size && juegosCompletados[i] == (i + 1).toString() -> {
                        botones[i].setBackgroundResource(R.drawable.background_boton_acierto)
                    }
                    else -> {
                        botones[i].setBackgroundResource(R.drawable.background_botones_juego_design)
                    }
                }
            }




            if (infoTablero.cambioJugador) {
                viewModel.cambiarJugador()
            }

            val victoria = viewModel.obtenerJugadorVictoria()

            if (victoria) {

                alertaVictoria()
            }

        })

        // Observa los cambios en la lista de jugadores
        viewModel.jugadores.observe(viewLifecycleOwner, Observer { jugadores ->
            jugadores.forEach { jugador ->
                // Observa los cambios en la posición del jugador actual
                viewModel.getPosicionJugadorLiveData(jugador.id)
                    .observe(viewLifecycleOwner, Observer { nuevaPosicion ->
                        actualizarPosicionJugadorUI(nuevaPosicion)
                        if (jugar) {
                            inicioMiniJuego(numMinijuego)
                            jugar = false
                        }
                    })
            }
        })


        findNavController()
            .currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<InformacionTablero>(ConstantesNavegacion.infoTableroKey)
            ?.observe(viewLifecycleOwner) { info ->
                if (info.cambioJugador) {
                    viewModel.cambiarJugador()
                }

                viewModel.actualizarPuntosJugador(
                    jugadorId = info.jugador,
                    juego1 = info.resultadoRepaso,
                    juego2 = info.resultadoMemory,
                    juego3 = info.resultadoTest,
                    juego4 = info.resutadoAhorcado,
                    juego5 = info.resultadoPruebaFinal
                )

                txtPuntosJugador.text = "juegos Completados=${viewModel.actualizarTextoPuntosJugador(jugador)}"

            }

        // Observa los cambios en el jugador actual

        viewModel.jugadorActual.observe(viewLifecycleOwner, Observer
        { nuevoJugador ->
                jugador = nuevoJugador
                txtJugadorActivo.text = "Jugador: $jugador"
                txtPuntosJugador.text =
                    "Minijuegos Completados: \n${viewModel.actualizarTextoPuntosJugador(jugador)}"

            viewModel.getPosicionJugadorLiveData(jugador).observe(viewLifecycleOwner, Observer { nuevaPosicion ->
                actualizarPosicionJugadorUI(nuevaPosicion)
            })

        })


        // Agrega el OnClickListener al botón para lanzar el dado
        btnLanzarDado.setOnClickListener {
            if (!NetworkUtils.isConnected(requireContext())) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Sin conexión a Internet")
                builder.setMessage("Por favor, comprueba tu conexión a Internet y vuelve a intentarlo, si das a aceptar sin internet se cerrará la aplicación.")
                builder.setCancelable(false)

                builder.setPositiveButton("Aceptar") { _, _ ->
                    if (NetworkUtils.isConnected(requireContext())) {
                        dialog?.dismiss()

                        jugar = true
                        // Genera un número aleatorio del 1 al 6
                        val numeroAleatorio = (1..6).random()

            // Muestra un AlertDialog con el número aleatorio
            //mostrarNumeroAleatorioDialog(numeroAleatorio)

                        // Mueve el jugador en el tablero según el número aleatorio
                        moverJugadorEnTablero(numeroAleatorio)
                    } else {
                        (context as? MainActivity)?.cerrarApp()
                    }
                }

                dialog = builder.create()
                dialog?.show()
            } else {

                jugar = true
                // Genera un número aleatorio del 1 al 6
                val numeroAleatorio = (1..6).random()

                // Mueve el jugador en el tablero según el número aleatorio
                moverJugadorEnTablero(numeroAleatorio)

            }

        }


        btnGuardarPartida.setOnClickListener {
            if (partidaCargada > 0) {
                mostrarDialogSobrescribirNuevaPartida()
            } else {
                mostrarDialogNuevaPartida()
            }
        }


        btnCargarPartida.setOnClickListener {


            // Creamos un Intent para iniciar VistaSeleccionPartida.
            val intent = Intent(requireContext(), VistaSeleccionPartida::class.java)

            // Lanzamos la actividad con el launcher que espera un resultado.
            startForResultCargarPartida.launch(intent)

        }

        btnGuardarPartidaExistente.setOnClickListener {


            // Creamos un Intent para iniciar VistaSeleccionPartida.
            val intent = Intent(requireContext(), VistaSeleccionPartida::class.java)

            // Lanzamos la actividad con el launcher que espera un resultado.
            startForResultGuardarPartidaExistente.launch(intent)

        }

        // Agrega el OnBackPressedCallback al fragmento para evitar que se cierre la aplicación al pulsar el botón "Atrás"
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // No hace nada
            }
        })

    }

    private fun mostrarDialogSobrescribirNuevaPartida() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Guardar Partida")
        builder.setMessage("¿Desea sobrescribir la partida existente o crear una nueva?")

        builder.setPositiveButton("Sobrescribir") { _, _ ->
            // Lógica para sobrescribir la partida
            val partidaCargadaLong: Long = partidaCargada.toLong()
            viewModel.guardarOActualizarPartida(partidaCargadaLong)
        }

        builder.setNegativeButton("Nueva") { _, _ ->
            // Lógica para crear una nueva partida
            viewModel.guardarPartida()

            val partidaCargadaLong = viewModel.obtenerUltimoIdPartidaDesdeBDAsync()
            partidaCargada = partidaCargadaLong.toString().toInt()

        }

        builder.setNeutralButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun mostrarDialogNuevaPartida() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Guardar Partida")
        builder.setMessage("¿Desea crear una nueva partida?")

        builder.setPositiveButton("Sí") { _, _ ->
            // Lógica para crear una nueva partida
            viewModel.guardarPartida()

            // Actualizar el valor de partidaCargada
            val partidaCargadaLong = viewModel.obtenerUltimoIdPartidaDesdeBDAsync()
            Log.d("VistaTableroFragment", "partidaCargadaLong: $partidaCargadaLong")
            partidaCargada = partidaCargadaLong.toString().toInt()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun alertaVictoria() {
        val builder = AlertDialog.Builder(requireContext())

        val inflate = layoutInflater
        val dialogView = inflate.inflate(R.layout.vista_victoria_layout,null)

        // Botón para ir al menú principal
        dialogView.findViewById<Button>(R.id.btnMenuPrincipal).setOnClickListener{
           // findNavController().navigate(R.id.vistaInicioMenu)
        }
        // Botón para salir y destruir la aplicación
        dialogView.findViewById<Button>(R.id.btnSalir).setOnClickListener{
            (context as? MainActivity)?.cerrarApp()
        }
        dialogView.findViewById<TextView>(R.id.tvJugador).text = "¡Ganador el Jugador $jugador!"

        builder.setView(dialogView)
        builder.setCancelable(false)

        builder.show()
    }

    /*private fun mostrarNumeroAleatorioDialog(numero: Int) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Número Aleatorio")
        alertDialogBuilder.setMessage("El número aleatorio es: $numero")
        alertDialogBuilder.setPositiveButton("Aceptar") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }*/

    private fun alertaBienvenida() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)
        builder.setTitle("1 Vs 1")
        builder.setMessage(
            "Bienvenido al trivial. Tu mision es completar todos los minijuegos para ganar la partida." +
                    "\n\nEn el tablero hay 4 minijuegos, y una vez completes todos, llegaras a la pregunta final. Se el primero en acertarla y ganaras la partida." +
                    "\n\n¡Mucha suerte!\n\nAcepta para continuar."
        )
        builder.setPositiveButton("Aceptar") { _, _ ->
            // Empieza la partida
        }
        builder.show()
    }

    /**
     * Actualiza la interfaz de usuario con el nuevo tablero.
     * @param tablero Matriz que representa el estado actual del tablero.
     */
    private fun actualizarTableroUI(tablero: Array<Array<String>>) {
        // Obtiene una referencia al GridLayout
        val gridLayout = view?.findViewById<GridLayout>(R.id.gridTablero)

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

                // Agrega un listener de clic al botón (opcional, según la lógica de tu aplicación)
                boton.setOnClickListener {
                    // Puedes agregar lógica de clic aquí si es necesario
                }

                // Agrega el botón al GridLayout solo si tiene un texto no vacío
                if (boton.text != "") {
                    gridLayout?.addView(boton)
                }

                boton
            }
        }
    }

    /**
     * Actualiza la posición del jugador en la interfaz de usuario.
     * @param nuevaPosicion Nueva posición del jugador.
     */
    private fun actualizarPosicionJugadorUI(nuevaPosicion: Pair<Int, Int>) {
        // Restablece el color de la última posición del jugador a blanco
        val ultimaFila = ultimaPosicionJugador.first
        val ultimaColumna = ultimaPosicionJugador.second

        botones[ultimaFila][ultimaColumna].setBackgroundResource(R.drawable.background_boton_tablero_usado)

        // Actualiza la última posición del jugador
        ultimaPosicionJugador = nuevaPosicion

        // Recoge el valor del texto de la celda y se guarda en la variable como un int
        numMinijuego = botones[nuevaPosicion.first][nuevaPosicion.second].text.toString().toInt()

        // Verifica si la nueva posición coincide con la posición de otro jugador
        if (botones[nuevaPosicion.first][nuevaPosicion.second].text == "OtroJugador") {
            // Pinta el botón de rojo en la nueva posición del jugador
            botones[nuevaPosicion.first][nuevaPosicion.second].setBackgroundResource(R.drawable.background_jugador1y2)
        } else {
            // Pinta el botón de negro en la nueva posición del jugador
            botones[nuevaPosicion.first][nuevaPosicion.second].setBackgroundResource(R.drawable.background_boton_tablero_usado)
        }

    }

    /**
     * Mueve al jugador en el tablero según el número obtenido al lanzar el dado.
     * @param numeroCasillas Número de casillas que el jugador debe avanzar.
     */
    private fun moverJugadorEnTablero(numeroCasillas: Int) {
        // Llama a la función del ViewModel para mover al jugador
        viewModel.moverJugador(numeroCasillas)
    }

    private fun inicioMiniJuego(casilla: Int) {

        when (1) {

            1 -> {
                // Navega al fragmento de vistaRepasoView cuando se hace clic en el botón
                findNavController().navigate(
                    VistaTableroFragmentDirections.navegarVistaRepaso(
                        Jugador = jugador
                    )
                )
            }

            2 -> {
                // Navega al fragmento de vistaMemoryView cuando se hace clic en el botón
                findNavController().navigate(
                    VistaTableroFragmentDirections.navegarVistaMemory(
                        Jugador = jugador
                    )
                )
            }

            3 -> {
                // Navega al fragmento de vistaJuegoView cuando se hace clic en el botón
                findNavController().navigate(
                    VistaTableroFragmentDirections.navegarVistaJuego(
                        Jugador = jugador
                    )
                )
            }

            4 -> {
                // Navega al fragmento de vistaAhorcadoView cuando se hace clic en el botón
                findNavController().navigate(
                    VistaTableroFragmentDirections.navegarAhorcadoVista(
                        Jugador = jugador
                    )
                )
            }

            5 -> {
                // Navega al fragmento de vistaPregFinalView cuando se hace clic en el botón
                findNavController().navigate(
                    VistaTableroFragmentDirections.navegarVistaPreguntaFinal(
                        Jugador = jugador
                    )
                )
            }

            else -> {
                // No hace nada
            }

        }
    }

}