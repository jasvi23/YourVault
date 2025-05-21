package com.example.yourvault.ui.fragments

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
import com.example.yourvault.network.RetrofitClient
import com.example.yourvault.network.models.LoginRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.Base64
import android.util.Log
import com.example.yourvault.fragments.ui.ForgotPasswordActivity
import com.example.yourvault.security.CryptoManager
import com.example.yourvault.ui.MainActivity
import java.security.SecureRandom

class LoginActivity: AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etPass:  EditText
    private lateinit var btnLogin: Button
    private lateinit var tvForgot: TextView

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etUsername)
        etPass  = findViewById(R.id.etPassword)
        btnLogin= findViewById(R.id.btnLogin)
        tvForgot= findViewById(R.id.tvForgot)
        val btnRegister = findViewById<Button>(R.id.btnGoToRegister)

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        tvForgot.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim() //campo email
            val pw    = etPass.text.toString() //campo password
            if (email.isEmpty()||pw.isEmpty()) { //si los campos estan vacios
                Toast.makeText(this,"Rellena todos los campos",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            CoroutineScope(Dispatchers.IO).launch {
                val resp = RetrofitClient.api.login(LoginRequest(email, pw))
                if (resp.isSuccessful) { // implica que els erver funciona 200 OK
                    val loginResp = resp.body()
                    if (loginResp?.error != null) {
                        // El servidor respondió con error, aunque el HTTP sea 200
                        runOnUiThread {
                            Toast.makeText(this@LoginActivity, loginResp.error, Toast.LENGTH_SHORT).show()
                        }
                    } else if (loginResp?.userId != null && loginResp.masterKeyHash != null) {
                        // Login válido → ahora sí derivamos clave y guardamos datos
                        val prefs = getSharedPreferences("vault_prefs", Context.MODE_PRIVATE)
                        prefs.edit().apply {
                            putInt("USER_ID", loginResp.userId)
                            putString("EMAIL", email)
                            apply()
                        }
                        val id = prefs.getInt("USER_ID", -1)
                        Log.d("FUNCIONA_USER_ID", "Guardado USER_ID = $id")

                        val masterkey = CryptoUtils.keyFromBase64(loginResp.masterKeyHash)

                        runOnUiThread {
                            CryptoManager.masterKey = masterkey // o decode si es base64
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@LoginActivity, "Respuesta inválida del servidor", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Error de conexión con el servidor", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
    }
}

