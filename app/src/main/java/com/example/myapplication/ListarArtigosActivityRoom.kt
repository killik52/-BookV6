package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class ListarArtigosActivityRoom : AppCompatActivity() {

    private var listViewArtigos: ListView? = null
    private lateinit var artigoViewModel: ArtigoViewModel
    private val artigosList = mutableListOf<ArtigoItem>()
    private lateinit var adapter: ArrayAdapter<ArtigoItem>

    data class ArtigoItem(val id: Long, val nome: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_artigos)

        try {
            // Inicializa o ViewModel
            artigoViewModel = ViewModelProvider(this)[ArtigoViewModel::class.java]
        } catch (e: Exception) {
            Log.e("ListarArtigos", "Erro ao inicializar ViewModel: ${e.message}")
            showToast("Erro ao inicializar o ViewModel: ${e.message}")
            finish()
            return
        }

        listViewArtigos = findViewById(R.id.listViewArtigos)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, artigosList)
        listViewArtigos?.adapter = adapter
        carregarArtigos()
    }

    private fun carregarArtigos() {
        artigoViewModel.getAllArtigos().observe(this) { artigos ->
            artigos?.let { listaArtigos ->
                adapter.submitList(listaArtigos)
                Log.d("ListarArtigosActivityRoom", "Carregados ${listaArtigos.size} artigos")
            }
        }
    }

    private fun buscarArtigos(query: String) {
        if (query.isEmpty()) {
            carregarArtigos()
        } else {
            artigoViewModel.searchArtigos(query).observe(this) { artigos ->
                artigos?.let { listaArtigos ->
                    adapter.submitList(listaArtigos)
                    Log.d("ListarArtigosActivityRoom", "Encontrados ${listaArtigos.size} artigos para '$query'")
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
} 