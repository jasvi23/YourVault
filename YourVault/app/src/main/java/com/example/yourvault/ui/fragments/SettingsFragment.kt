package com.example.yourvault.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.yourvault.R

class SettingsFragment: PreferenceFragmentCompat() {
    //Ahora lo que hace es usar un xml preference que hemos creado, y que por defecto en Adnroid kotlin tiene todo lo necesario para
    // las funciones de configuracion.
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        // switch de notificaciones
        findPreference<SwitchPreferenceCompat>("notifications_enabled")?.setOnPreferenceChangeListener { pref, newValue ->
            val enable = newValue as Boolean
            requireContext().getSharedPreferences("vault_prefs", 0)
                .edit()
                .putBoolean("notifications_enabled", enable)
                .apply()
            true
        }
        //SOPORTE
        findPreference<Preference>("contact_support")?.setOnPreferenceClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:juananm2395@gmail.com")
                putExtra(Intent.EXTRA_SUBJECT, "Consulta desde YourVault")
            }
            startActivity(Intent.createChooser(intent, "Enviar correo con..."))
            true
        }
        // logout FUNCIONA
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
