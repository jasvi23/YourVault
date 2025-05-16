package com.example.yourvault.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yourvault.R
import com.example.yourvault.security.CryptoUtils
import com.example.yourvault.ui.MainActivity
import com.example.yourvault.network.RetrofitClient
import com.example.yourvault.network.models.LoginRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.Base64
import com.example.yourvault.security.CryptoManager
import java.security.SecureRandom

class LoginActivity: AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etPass:  EditText
    private lateinit var btnLogin: Button
    private lateinit var tvForgot: TextView

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etUsername)   // :contentReference[oaicite:3]{index=3}
        etPass  = findViewById(R.id.etPassword)
        btnLogin= findViewById(R.id.btnLogin)
        tvForgot= findViewById(R.id.tvForgot)

        tvForgot.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val pw    = etPass.text.toString()
            if (email.isEmpty()||pw.isEmpty()) {
                Toast.makeText(this,"Rellena todos los campos",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            CoroutineScope(Dispatchers.IO).launch {
                val resp = RetrofitClient.api.login(LoginRequest(email,pw))
                if (resp.isSuccessful) {
                    // 1) Generar/leer salt
                    val prefs = getSharedPreferences("vault_prefs", Context.MODE_PRIVATE)
                    val saltB = prefs.getString("KEY_SALT",null)
                        ?.let{ Base64.decode(it,Base64.NO_WRAP) }
                        ?: ByteArray(16).also {
                            SecureRandom().nextBytes(it)
                            prefs.edit()
                                .putString("KEY_SALT",Base64.encodeToString(it,Base64.NO_WRAP))
                                .apply()
                        }
                    // 2) Derivar clave maestra
                    val key = CryptoUtils.deriveKey(pw,saltB)

                    // 3) Guardar ID usuario
                    prefs.edit().putInt("USER_ID", resp.body()!!.userId).apply()

                    // 4) Lanzar MainActivity pasando masterKey en un singleton
                    runOnUiThread {
                        CryptoManager.masterKey = key
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity,
                            "Credenciales inválidas",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

