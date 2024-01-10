package com.pmg.proyecto_kahoot_pmg_sgg.feature.vistaInicioJuego

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.pmg.proyecto_kahoot_pmg_sgg.R
import com.pmg.proyecto_kahoot_pmg_sgg.core.domain.model.jugador.InformacionTablero
import com.pmg.proyecto_kahoot_pmg_sgg.feature.vistaJuego.VistaJuegoViewModel

class VistaMenuCompletoFragment : Fragment() {

    // ViewModel asociado a la vista
    private val viewModel: VistaJuegoViewModel by viewModels()

    // Botones en la vista
    private lateinit var btnJugarLocal: Button
    private lateinit var btnJugarMultijugador: Button
    private lateinit var btnAjustes: Button
    private lateinit var btnSalir: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Infla el diseño de la vista del menú completo
        val viewInicio = inflater.inflate(R.layout.fragment_vista_menu_completo_view, container, false)

        // Asigna referencias a los botones
        btnJugarLocal = viewInicio.findViewById(R.id.btn_JuegoLocal)
        btnJugarMultijugador = viewInicio.findViewById(R.id.btn_JuegoMultijugador)
        btnAjustes = viewInicio.findViewById(R.id.btn_Ajustes)
        btnSalir = viewInicio.findViewById(R.id.btn_Salir)

        // Agrega OnClickListener al botón btnJugarLocal
        btnJugarLocal.setOnClickListener {
            // Muestra un Toast cuando se hace clic en el botón
            //Toast.makeText(requireContext(), "Clic en Jugar Local", Toast.LENGTH_SHORT).show()
            // Navega al fragmento de vistaTableroView cuando se hace clic en el botón
            findNavController().navigate(VistaMenuCompletoFragmentDirections.navegarVistaTablero(InformacionTablero(jugador = 1)))
        }

        // Puedes agregar lógica adicional y OnClickListener a otros botones aquí

        return viewInicio
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Agrega el OnBackPressedCallback al fragmento para evitar que se cierre la aplicación al pulsar el botón "Atrás"
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Alerta de que si quiere salir de la aplicación
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Salir")
                builder.setMessage("¿Estás seguro de que quieres salir de la aplicación?")
                builder.setPositiveButton("Sí") { _, _ ->
                    // Cierra la aplicación
                    requireActivity().finish()
                }
                builder.setNegativeButton("No") { _, _ ->
                    // No hace nada
                }
                builder.show()
            }
        })
    }
}