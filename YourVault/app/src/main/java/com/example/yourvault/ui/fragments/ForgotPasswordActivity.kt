package com.example.yourvault.fragments.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yourvault.R
import com.example.yourvault.network.RetrofitClient
import com.example.yourvault.network.models.RegisterRequest
import com.example.yourvault.ui.fragments.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etNew:   EditText
    private lateinit var etConfirm: EditText
    private lateinit var btnReset: Button
    private lateinit var btnCancel: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)

        etEmail   = findViewById(R.id.etEmail)
        etNew     = findViewById(R.id.etNewPassword)
        etConfirm = findViewById(R.id.etConfirmPassword)
        btnReset  = findViewById(R.id.btnReset)
        btnCancel = findViewById(R.id.btnCancel)

        btnReset.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password    = etNew.text.toString()
            val confirm    = etConfirm.text.toString()

            if (email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.length < 8) {
                Toast.makeText(this, "La contraseña debe tener ≥8", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password != confirm) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                val resp = RetrofitClient.api.changePassword(RegisterRequest(email, password))
                if (resp.isSuccessful) {
                    val body = resp.body()
                    if (body?.error != null) {
                        runOnUiThread {
                            Toast.makeText(this@ForgotPasswordActivity, body.error, Toast.LENGTH_SHORT).show()
                        }
                    } else if (body?.success != null) {
                        runOnUiThread {
                            Toast.makeText(this@ForgotPasswordActivity, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@ForgotPasswordActivity, LoginActivity::class.java))
                            finish()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@ForgotPasswordActivity, "Error inesperado", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@ForgotPasswordActivity, "Error de conexión con el servidor", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        btnCancel.setOnClickListener { finish() }
    }
}
