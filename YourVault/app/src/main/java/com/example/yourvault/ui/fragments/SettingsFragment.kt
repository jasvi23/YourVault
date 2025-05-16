package com.example.yourvault.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.yourvault.R
import com.example.yourvault.ui.LoginActivity

class SettingsFragment: PreferenceFragmentCompat() {
    //Ahora lo que hace es usar un xml preference que hemos creado, y que por defecto en Adnroid kotlin tiene todo lo necesario para
    // las funciones de configuracion.
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        // switch de notificaciones
        findPreference<SwitchPreferenceCompat>("notifications_enabled")?.setOnPreferenceChangeListener { pref, new ->
            // Aquí solo guardamos el flag; las llamadas a NotificationUtils leerán este valor
            true
        }

        // logout
        findPreference<androidx.preference.Preference>("logout")?.setOnPreferenceClickListener {
            // limpiar sesion
            requireContext()
                .getSharedPreferences("vault_prefs", 0)
                .edit()
                .remove("session_token")
                .apply()
            // volver a loginActivity
            startActivity(Intent(requireContext(), LoginActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))
            activity?.finish()
            true
        }
    }
}
