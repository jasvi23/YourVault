package com.example.yourvault.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yourvault.R
import com.example.yourvault.network.RetrofitClient
import com.example.yourvault.network.models.ChangePasswordRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etNew:   EditText
    private lateinit var etConfirm: EditText
    private lateinit var btnReset: Button
    private lateinit var btnCancel: Button

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        setContentView(R.layout.activity_forgot)

        etEmail   = findViewById(R.id.etEmail)
        etNew     = findViewById(R.id.etNewPassword)
        etConfirm = findViewById(R.id.etConfirmPassword)
        btnReset  = findViewById(R.id.btnReset)
        btnCancel = findViewById(R.id.btnCancel)

        btnReset.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val p1    = etNew.text.toString()
            val p2    = etConfirm.text.toString()

            if (email.isEmpty() || p1.isEmpty() || p2.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (p1.length < 8) {
                Toast.makeText(this, "La contraseña debe tener ≥8", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (p1 != p2) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                val resp = RetrofitClient.api.changePassword(
                    ChangePasswordRequest(email, p1)
                )
                runOnUiThread {
                    if (resp.isSuccessful) {
                        Toast.makeText(this@ForgotPasswordActivity,
                            "Contraseña cambiada", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@ForgotPasswordActivity,
                            resp.errorBody()?.string() ?: "Error", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        btnCancel.setOnClickListener { finish() }
    }
}
