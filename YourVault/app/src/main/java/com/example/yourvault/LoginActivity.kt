package com.example.yourvault

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnLogin: Button = findViewById(R.id.btnLogin)
        val btnGoToRegister: Button = findViewById(R.id.btnGoToRegister)

        btnLogin.setOnClickListener {
            // Aquí podrías agregar la validación de usuario antes de proceder
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        btnGoToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
