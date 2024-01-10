package com.pmg.proyecto_kahoot_pmg_sgg.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pmg.proyecto_kahoot_pmg_sgg.R
import com.pmg.proyecto_kahoot_pmg_sgg.app.utils.AlertaPreferencias
import com.pmg.proyecto_kahoot_pmg_sgg.feature.vistaTablero.VistaTableroFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AlertaPreferencias.setDialogShown(this, false)
    }

}