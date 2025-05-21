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
import com.example.yourvault.util.NotificationUtils
import com.example.yourvault.util.PasswordChecker
import com.example.yourvault.util.PasswordGenerator
import com.google.android.material.textfield.TextInputEditText

class GeneratorFragment: Fragment(R.layout.fragment_generator) {
    private lateinit var etPass: TextInputEditText
    private lateinit var tvLength: TextView
    private lateinit var tvScore: TextView
    private lateinit var tvWarnings: TextView
    private lateinit var segs: List<View>
    private lateinit var inds: List<List<View>>
    private lateinit var btnGen: Button
    private lateinit var btnCopy: Button

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v,s)

        etPass = v.findViewById(R.id.password_edit_text)
        tvLength = v.findViewById(R.id.length_label)
        tvScore = v.findViewById(R.id.password_score)
        tvWarnings = v.findViewById(R.id.password_warnings)

        segs = listOf(
            v.findViewById(R.id.security_segment_1),
            v.findViewById(R.id.security_segment_2),
            v.findViewById(R.id.security_segment_3),
            v.findViewById(R.id.security_segment_4),
            v.findViewById(R.id.security_segment_5)
        )
        inds = listOf( //para que funcione la doble bombilla
            listOf(
                v.findViewById(R.id.lowercase_indicator),
                v.findViewById(R.id.lowercase_indicator2)
            ),
            listOf(
                v.findViewById(R.id.uppercase_indicator),
                v.findViewById(R.id.uppercase_indicator2)
            ),
            listOf(
                v.findViewById(R.id.numbers_indicator),
                v.findViewById(R.id.numbers_indicator2)
            ),
            listOf(
                v.findViewById(R.id.symbols_indicator),
                v.findViewById(R.id.symbols_indicator2)
            )
        )
        btnGen = v.findViewById(R.id.generate_button)
        btnCopy= v.findViewById(R.id.copy_button)

        // Al escribir
        etPass.addTextChangedListener { et ->
            val pwd = et?.toString().orEmpty()
            tvLength.text = "Longitud: ${pwd.length}"
            val result = PasswordChecker.check(pwd)
            tvScore.text = "Score: ${result.score}" //comprobacion funciona
            tvWarnings.text = result.warnings.joinToString("\n") //comprobacion funciona

            segs.forEachIndexed { i, view ->
                view.setBackgroundColor(if ((i+1)*2 <= result.score) Color.GREEN else Color.RED)
            }

            val counts = listOf(
                pwd.count { it.isLowerCase() },
                pwd.count { it.isUpperCase() },
                pwd.count { it.isDigit() },
                pwd.count { !it.isLetterOrDigit() }
            )

            counts.forEachIndexed { i, count ->
                inds[i][0].setBackgroundColor(if (count >= 1) Color.GREEN else Color.RED)
                inds[i][1].setBackgroundColor(if (count >= 2) Color.GREEN else Color.RED)
            }
        }

        btnGen.setOnClickListener {
            val np = PasswordGenerator.generate()
            etPass.setText(np)
        }

        btnCopy.setOnClickListener {
            val cm = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val cd = ClipData.newPlainText("pwd", etPass.text.toString())
            cm.setPrimaryClip(cd)
            if (NotificationUtils.shouldNotify(requireContext())) {
                NotificationUtils.showNotification(requireContext(), "YourVault", etPass.text.toString())
            }
            Toast.makeText(requireContext(),"Contraseña copiada", Toast.LENGTH_SHORT).show()
        }
    }
}
