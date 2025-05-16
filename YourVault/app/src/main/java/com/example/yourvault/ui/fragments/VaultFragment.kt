package com.example.yourvault.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yourvault.R
import com.example.yourvault.data.PasswordDbHelper
import com.example.yourvault.data.models.PasswordEntry
import com.example.yourvault.security.CryptoManager
import com.example.yourvault.ui.PasswordAdapter
import com.example.yourvault.util.NotificationUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton

class VaultFragment : Fragment(R.layout.fragment_vault) {
    private lateinit var db: PasswordDbHelper
    private lateinit var adapter: PasswordAdapter
    private lateinit var rvPasswords: RecyclerView
    private lateinit var fabAdd: FloatingActionButton

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)
        db = PasswordDbHelper(requireContext(), CryptoManager.masterKey)

        adapter = PasswordAdapter(mutableListOf()) { entry, action ->
            when(action) {
                PasswordAdapter.Action.EDIT   -> showEditDialog(entry)
                PasswordAdapter.Action.DELETE -> {
                    db.delete(entry.id)
                    loadAll()
                    Toast.makeText(requireContext(), "Entrada eliminada", Toast.LENGTH_SHORT).show()
                }
            }
        }

        rvPasswords.layoutManager = LinearLayoutManager(requireContext())
        rvPasswords.adapter = adapter
        loadAll()
    }

    private fun loadAll() {
        val list = db.getAll()
        adapter.updateList(list)
    }

    private fun showEditDialog(entry: PasswordEntry) {
        val ctx = requireContext()
        val layout = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }
        val etName = EditText(ctx).apply {
            hint = "Nombre"
            setText(entry.name)
        }
        val etPass = EditText(ctx).apply {
            hint = "Contraseña"
            setText(entry.passwordHash)
        }
        layout.addView(etName)
        layout.addView(etPass)

        AlertDialog.Builder(ctx)
            .setTitle("Editar entrada")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                val newName = etName.text.toString().trim()
                val newPass = etPass.text.toString()
                if (newName.isEmpty() || newPass.isEmpty()) {
                    Toast.makeText(ctx, "Rellena ambos campos", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                db.update( //referencia a passwordentry
                    PasswordEntry(
                        id = entry.id,
                        name = newName,
                        username = entry.username,   // o "" si no usas username
                        passwordHash = newPass
                    )
                )
                loadAll()
                Toast.makeText(ctx, "Entrada actualizada", Toast.LENGTH_SHORT).show()
                // Dentro de showEditDialog, justo tras Toast:
                if (NotificationUtils.shouldNotify(ctx)) {
                    NotificationUtils.showNotification(ctx, "YourVault", "Entrada actualizada")
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}

