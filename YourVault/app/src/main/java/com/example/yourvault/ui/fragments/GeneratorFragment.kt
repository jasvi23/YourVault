package com.example.yourvault.ui.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.yourvault.R
import com.example.yourvault.util.PasswordChecker
import com.example.yourvault.util.PasswordGenerator
import com.google.android.material.textfield.TextInputEditText

class GeneratorFragment: Fragment(R.layout.fragment_generator) {
    private lateinit var etPass: TextInputEditText
    private lateinit var tvLength: TextView
    private lateinit var segs: List<View>
    private lateinit var inds: List<View>
    private lateinit var btnGen: Button
    private lateinit var btnCopy: Button

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v,s)
        etPass = v.findViewById(R.id.password_edit_text)      // :contentReference[oaicite:5]{index=5}
        tvLength = v.findViewById(R.id.length_label)
        segs = listOf(
            v.findViewById(R.id.security_segment_1),
            v.findViewById(R.id.security_segment_2),
            v.findViewById(R.id.security_segment_3),
            v.findViewById(R.id.security_segment_4),
            v.findViewById(R.id.security_segment_5)
        )
        inds = listOf(
            v.findViewById(R.id.lowercase_indicator),
            v.findViewById(R.id.uppercase_indicator),
            v.findViewById(R.id.numbers_indicator),
            v.findViewById(R.id.symbols_indicator)
        )
        btnGen = v.findViewById(R.id.generate_button)
        btnCopy= v.findViewById(R.id.copy_button)

        // Al escribir
        etPass.addTextChangedListener { et ->
            val pwd = et?.toString().orEmpty()
            // Longitud
            tvLength.text = "Longitud: ${pwd.length}"
            // Checker 10 pts en 5 áreas
            val r = PasswordChecker.check(pwd)                  // :contentReference[oaicite:7]{index=7}
            // Actualiza segmentos (2 puntos cada uno)
            segs.forEachIndexed { i, view ->
                view.setBackgroundColor(if ((i+1)*2 <= r.score) Color.GREEN else Color.RED)
            }
            // Indicadores de requisitos
            val checks = listOf(
                pwd.any{it.isLowerCase()},
                pwd.any{it.isUpperCase()},
                pwd.any{it.isDigit()},
                pwd.any{!it.isLetterOrDigit()}
            )
            inds.forEachIndexed { i, view ->
                view.setBackgroundColor(if (checks[i]) Color.GREEN else Color.RED)
            }
        }

        btnGen.setOnClickListener {
            val np = PasswordGenerator.generate()
            etPass.setText(np)
        }

        btnCopy.setOnClickListener {
            val cm = requireContext().getSystemService(Context.CLIPBOARD_SERVICE)
                    as ClipboardManager
            val cd = ClipData.newPlainText("pwd", etPass.text.toString())
            cm.setPrimaryClip(cd)
            Toast.makeText(requireContext(),"Contraseña copiada", Toast.LENGTH_SHORT).show()
        }
    }
}
