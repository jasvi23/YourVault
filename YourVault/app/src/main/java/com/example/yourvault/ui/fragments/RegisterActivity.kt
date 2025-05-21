package com.example.yourvault.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yourvault.R
import com.example.yourvault.network.RetrofitClient
import com.example.yourvault.network.models.RegisterRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etPass:  EditText
    private lateinit var etConfirm: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnCancel:   Button

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        setContentView(R.layout.activity_register)

        etEmail   = findViewById(R.id.etEmail)
        etPass    = findViewById(R.id.etPassword)
        etConfirm = findViewById(R.id.etConfirm)
        btnRegister= findViewById(R.id.btnRegister)
        btnCancel  = findViewById(R.id.btnCancel)

        btnRegister.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val p1    = etPass.text.toString()
            val p2    = etConfirm.text.toString()

            // VALIDACIONES CLIENTE:
            if (email.isEmpty() || p1.isEmpty() || p2.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (p1.length < 8) {
                Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (p1 != p2) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Podrías añadir chequeo de mayúsculas, minúsculas, dígitos y símbolos aquí con PasswordChecker.

            // Llamada al servidor (solo registra email + password; la complejidad la ignora)
            CoroutineScope(Dispatchers.IO).launch {
                val resp = RetrofitClient.api.register(RegisterRequest(email, p1))
                if (resp.isSuccessful) {
                    val body = resp.body()
                    if (body?.error != null) {
                        if (body.error.contains("El email ya está registrado", ignoreCase = true)) {
                            runOnUiThread {
                                Toast.makeText(this@RegisterActivity, "Este email ya existe", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(this@RegisterActivity, body.error, Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else if (body?.success != null) {
                        runOnUiThread {
                            Toast.makeText(this@RegisterActivity, body.success, Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@RegisterActivity, "Respuesta inválida del servidor", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@RegisterActivity, "Error de conexión con el servidor", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        btnCancel.setOnClickListener {
            finish()
        }
    }
}


