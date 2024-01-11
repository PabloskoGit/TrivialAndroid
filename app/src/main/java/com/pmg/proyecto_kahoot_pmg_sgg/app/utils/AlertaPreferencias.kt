package com.pmg.proyecto_kahoot_pmg_sgg.app.utils

import android.content.Context
import androidx.preference.PreferenceManager

class AlertaPreferencias {

    companion object {
        // Guarda si el dialogo de alerta se ha mostrado
        fun setDialogShown(context: Context, shown: Boolean) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            prefs.edit().putBoolean("dialogShown", shown).apply()
        }

        // Comprueba si el dialogo de alerta se ha mostrado o no (por defecto se muestra)
        fun wasDialogShown(context: Context): Boolean {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            return prefs.getBoolean("dialogShown", false)
        }
    }
}