package com.pmg.proyecto_kahoot_pmg_sgg.core.domain.model.Patida

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pmg.proyecto_kahoot_pmg_sgg.R

class PartidaAdapter(private val partidasList: List<Partida>) : RecyclerView.Adapter<PartidaAdapter.PartidaViewHolder>() {

    class PartidaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtPartida: TextView = itemView.findViewById(R.id.txtPartida)
        val txtIdPartida: TextView = itemView.findViewById(R.id.txtIdPartida)
        val txtJugador1: TextView = itemView.findViewById(R.id.txtJugador1)
        val txtJugador2: TextView = itemView.findViewById(R.id.txtJugador2)
        val txtPuntosJugador1: TextView = itemView.findViewById(R.id.txtPuntosJugador1)
        val txtPuntosJugador2: TextView = itemView.findViewById(R.id.txtPuntosJugador2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartidaViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.partida_cargar, parent, false)
        return PartidaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PartidaViewHolder, position: Int) {
        val currentPartida = partidasList[position]
        holder.txtPartida.text = "Partida"
        holder.txtIdPartida.text = currentPartida.idPartida.toString()
        holder.txtJugador1.text = "Jugador 1"
        holder.txtJugador2.text = "Jugador 2"
        holder.txtPuntosJugador1.text = currentPartida.puntosJugador1.toString()
        holder.txtPuntosJugador2.text = currentPartida.puntosJugador2.toString()
    }

    override fun getItemCount(): Int {
        return partidasList.size
    }
}