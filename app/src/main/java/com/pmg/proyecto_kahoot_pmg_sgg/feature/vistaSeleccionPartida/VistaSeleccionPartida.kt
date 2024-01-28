package com.pmg.proyecto_kahoot_pmg_sgg.feature.vistaSeleccionPartida

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pmg.proyecto_kahoot_pmg_sgg.core.data.persistencia.DatabaseHelper
import com.pmg.proyecto_kahoot_pmg_sgg.core.domain.model.partida.PartidaAdapter
import com.pmg.proyecto_kahoot_pmg_sgg.R
import com.pmg.proyecto_kahoot_pmg_sgg.core.domain.model.recyclerItemClickListener.RecyclerItemClickListener

class VistaSeleccionPartida : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dbHandler: DatabaseHelper
    // El método onCreate se llama cuando se crea la actividad.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Establecemos el layout para la actividad.
        setContentView(R.layout.lista_partidas)
        dbHandler = DatabaseHelper(this)
        recyclerView = findViewById(R.id.rvPartidas)

        // Obtiene la lista de discos de la base de datos.
        val partidaList = dbHandler.obtenerTodasLasPartidas()

        // Crea un adaptador para el RecyclerView y lo configura.
        val adapter = PartidaAdapter(partidaList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter


// Establecemos un listener que reaccionará cuando se haga clic en un ítem del RecyclerView.
        recyclerView.addOnItemTouchListener(
            RecyclerItemClickListener(
                this,
                recyclerView,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        // Creamos un nuevo Intent para enviar el resultado de vuelta.
                        val intent = Intent()
                        // Añadimos el ID de la partida seleccionada al Intent.
                        intent.putExtra("selectedPartidaId", partidaList[position].idPartida)
                        // Establecemos el resultado de la actividad con el Intent y RESULT_OK.
                        setResult(RESULT_OK, intent)
                        // Finalizamos la actividad, cerrándola y volviendo a la anterior.
                        finish()
                    }

                    override fun onLongItemClick(view: View?, position: Int) {
                        // Puedes manejar eventos de clic largo si es necesario.
                    }
                })
        )
    }
}