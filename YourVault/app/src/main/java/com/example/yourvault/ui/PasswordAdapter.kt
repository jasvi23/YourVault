package com.example.yourvault.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yourvault.R
import com.example.yourvault.data.models.PasswordEntry

class PasswordAdapter(
    private var items: MutableList<PasswordEntry>,
    private val listener: (PasswordEntry, Action) -> Unit
) : RecyclerView.Adapter<PasswordAdapter.VH>() {

    enum class Action { EDIT, DELETE }

    inner class VH(view: View): RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.password_name)
        val tvPassword: TextView = view.findViewById(R.id.password_value)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_password, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, pos: Int) {
        val entry = items[pos]
        holder.tvName.text = entry.name
        holder.tvPassword.text = entry.passwordHash
        holder.itemView.setOnLongClickListener {
            // Ejemplo: menú contextual con editar y eliminar
            listener(entry, Action.EDIT)
            true
        }
        holder.itemView.setOnClickListener {
            listener(entry, Action.DELETE)
        }
    }

    override fun getItemCount() = items.size

    fun updateList(newList: List<PasswordEntry>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }
}
