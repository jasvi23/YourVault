package com.example.yourvault.ui

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.yourvault.R
import com.example.yourvault.data.models.PasswordEntry
import com.example.yourvault.util.NotificationUtils

class PasswordAdapter(
    private var items: MutableList<PasswordEntry>,
    private val listener: (PasswordEntry, Action) -> Unit
) : RecyclerView.Adapter<PasswordAdapter.VH>() {

    enum class Action { EDIT, DELETE }

    inner class VH(view: View): RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.password_name)
        val tvPassword: TextView = view.findViewById(R.id.password_value)
        val btnCopy: ImageButton = view.findViewById(R.id.copy_password_button)
        val btnEdit: ImageButton = view.findViewById(R.id.edit_password_button)
        val btnDelete: ImageButton = view.findViewById(R.id.delete_password_button)
        val btnToggle: ImageButton = view.findViewById(R.id.view_password_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_password, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, pos: Int) {
        val entry = items[pos]
        val ctx = holder.itemView.context

        holder.tvName.text = entry.name
        holder.tvPassword.text = entry.passwordHash
        holder.tvPassword.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD

        holder.btnCopy.setOnClickListener {
            val password = holder.tvPassword.text.toString()
            val clipboard = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("password", password)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(ctx, "Contraseña copiada", Toast.LENGTH_SHORT).show()

            if (NotificationUtils.shouldNotify(ctx)) {
                NotificationUtils.showNotification(ctx, "YourVault", password)
            }
        }

        holder.btnEdit.setOnClickListener {
            listener(entry, Action.EDIT)
        }

        holder.btnDelete.setOnClickListener {
            AlertDialog.Builder(ctx) //mensaje confirmacion
                .setTitle("Eliminar entrada")
                .setMessage("¿Seguro que quieres eliminar esta contraseña?")
                .setPositiveButton("Sí") { _, _ ->
                    listener(entry, Action.DELETE)
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        holder.btnToggle.setOnClickListener {
            val showing = holder.tvPassword.inputType == android.text.InputType.TYPE_CLASS_TEXT
            holder.tvPassword.inputType = if (showing)
                android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            else
                android.text.InputType.TYPE_CLASS_TEXT
        }
    }

    override fun getItemCount() = items.size

    fun updateList(newList: List<PasswordEntry>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }
}
