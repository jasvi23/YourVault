package com.example.yourvault.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
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
        super.onViewCreated(v,s)

        rvPasswords = v.findViewById(R.id.rvPasswords)
        fabAdd = v.findViewById(R.id.add_password_fab)

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

        fabAdd.setOnClickListener {
            showAddDialog()
        }
    }

    private fun loadAll() {
        try {
            val prefs = requireContext().getSharedPreferences("vault_prefs", Context.MODE_PRIVATE)
            val user = prefs.getInt("USER_ID", -1).toString()
            val list = db.getAll(user)
            list.forEach {
                Log.d("VAULT_DB", "ID: ${it.id}, Servicio: ${it.name}, User: ${it.username}, Pass: ${it.passwordHash}")
            }
            adapter.updateList(list)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error al cargar contraseñas", Toast.LENGTH_LONG).show()
            Log.e("VAULT_DB", "Error: ${e.message}", e)
        }
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
            .setPositiveButton("Guardar", null)
            .setNegativeButton("Cancelar", null)
            .create().apply {
                setOnShowListener {
                    getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val newName = etName.text.toString().trim()
                        val newPass = etPass.text.toString()
                        if (newName.isEmpty() || newPass.isEmpty()) {
                            Toast.makeText(ctx, "Rellena ambos campos", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                        db.update(
                            PasswordEntry(
                                id = entry.id,
                                name = newName,
                                username = entry.username,
                                passwordHash = newPass
                            )
                        )
                        loadAll()
                        Toast.makeText(ctx, "Entrada actualizada", Toast.LENGTH_SHORT).show()
                        if (NotificationUtils.shouldNotify(ctx)) {
                            NotificationUtils.showNotification(ctx, "YourVault", "Entrada actualizada")
                        }
                        dismiss()
                    }
                }
            }.show()
    }

    private fun showAddDialog() {
        val ctx = requireContext()
        val layout = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }
        val etName = EditText(ctx).apply {
            hint = "Nombre"
        }
        val etPass = EditText(ctx).apply {
            hint = "Contraseña"
        }
        layout.addView(etName)
        layout.addView(etPass)

        AlertDialog.Builder(ctx)
            .setTitle("Nueva entrada")
            .setView(layout)
            .setPositiveButton("Guardar", null)
            .setNegativeButton("Cancelar", null)
            .create().apply {
                setOnShowListener {
                    getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val name = etName.text.toString().trim()
                        val pass = etPass.text.toString()
                        if (name.isEmpty() || pass.isEmpty()) {
                            Toast.makeText(ctx, "Nombre y contraseña son obligatorios", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                        val prefs = ctx.getSharedPreferences("vault_prefs", Context.MODE_PRIVATE)
                        val user = prefs.getInt("USER_ID", -1).toString()
                        db.insert(
                            PasswordEntry(
                                name = name,
                                username = user,
                                passwordHash = pass
                            )
                        )
                        loadAll()
                        Toast.makeText(ctx, "Contraseña añadida", Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                }
            }.show()
    }
}
