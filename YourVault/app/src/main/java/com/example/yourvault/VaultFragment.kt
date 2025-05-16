package com.example.yourvault

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class VaultFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var addPasswordButton: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar
        val view = inflater.inflate(R.layout.fragment_vault, container, false)

        recyclerView = view.findViewById(R.id.passwords_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        // aquí se inicializará el adaptador

        addPasswordButton = view.findViewById(R.id.add_password_fab)
        addPasswordButton.setOnClickListener {

            //lógica para añadir una nueva contraseña
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = VaultFragment()
    }
}