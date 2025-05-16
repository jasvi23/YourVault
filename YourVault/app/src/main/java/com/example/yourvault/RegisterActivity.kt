package com.example.yourvault
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnRegister: Button = findViewById(R.id.btnRegister)
        val btnCancel: Button = findViewById(R.id.btnCancel)

        // Tanto al registrar como al cancelar se redirige a la activity de login
        btnRegister.setOnClickListener {
            // Aquí agregarías la lógica de registro de usuario
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        btnCancel.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
