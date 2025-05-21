package com.example.yourvault.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.yourvault.R
import com.example.yourvault.security.CryptoManager
import com.example.yourvault.ui.fragments.GeneratorFragment
import com.example.yourvault.ui.fragments.LoginActivity
import com.example.yourvault.ui.fragments.SettingsFragment
import com.example.yourvault.ui.fragments.VaultFragment
import com.example.yourvault.util.NotificationUtils
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_generator -> {
                    loadFragment(GeneratorFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_vault -> {
                    loadFragment(VaultFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_settings -> {
                    loadFragment(SettingsFragment())
                    return@setOnItemSelectedListener true
                }
                else -> false
            }
        }
        //fragmento inicial
        loadFragment(GeneratorFragment())
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}