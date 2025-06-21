package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import database.entities.Artigo
import database.repository.ArtigoRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ListarArtigosActivityRoom : AppCompatActivity() {

    private var listViewArtigos: ListView? = null
    private lateinit var artigoViewModel: ArtigoViewModel
    private val artigosList = mutableListOf<ArtigoItem>()

    data class ArtigoItem(val id: Long, val nome: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_artigos)

        try {
            // Inicializa o ViewModel
            val repository = ArtigoRepository((application as MyApplication).database.artigoDao())
            val factory = ArtigoViewModelFactory(repository)
            artigoViewModel = ViewModelProvider(this, factory)[ArtigoViewModel::class.java]
        } catch (e: Exception) {
            Log.e("ListarArtigosRoom", "Erro ao inicializar ViewModel: ${e.message}")
            showToast("Erro ao inicializar o banco de dados: ${e.message}")
            finish()
            return
        }

        listViewArtigos = findViewById(R.id.listViewArtigos)
        carregarArtigos()
    }

    private fun carregarArtigos() {
        artigosList.clear()
        
        // Observa mudanças na lista de artigos usando Flow
        lifecycleScope.launch {
            artigoViewModel.allArtigos.collectLatest { artigos ->
                val nomesList = mutableListOf<String>()
                
                artigos.forEach { artigo ->
                    nomesList.add(artigo.nome ?: "Artigo sem nome")
                    artigosList.add(ArtigoItem(artigo.id, artigo.nome ?: "Artigo sem nome"))
                }
                
                // Cria um adaptador para o ListView
                val adapter = ArrayAdapter(this@ListarArtigosActivityRoom, android.R.layout.simple_list_item_1, nomesList)
                listViewArtigos?.adapter = adapter
                
                Log.d("ListarArtigosRoom", "Carregados ${artigos.size} artigos")
            }
        }

        // Adiciona listener para abrir a tela de edição
        listViewArtigos?.setOnItemClickListener { _, _, position, _ ->
            try {
                val artigoSelecionado = artigosList[position]
                
                // Busca o artigo completo no banco
                artigoViewModel.getArtigoById(artigoSelecionado.id,
                    onSuccess = { artigo ->
                        artigo?.let {
                            // Cria uma Intent para abrir a tela de edição
                            val intent = Intent(this, CriarNovoArtigoActivity::class.java).apply {
                                putExtra("artigo_id", it.id)
                                putExtra("nome_artigo", it.nome)
                                putExtra("valor", it.preco)
                                putExtra("quantidade", it.quantidade)
                                putExtra("numero_serial", it.numeroSerial)
                                putExtra("descricao", it.descricao)
                            }
                            startActivity(intent)
                        }
                    },
                    onError = { exception ->
                        Log.e("ListarArtigosRoom", "Erro ao buscar artigo: ${exception.message}")
                        showToast("Erro ao abrir artigo: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                Log.e("ListarArtigosRoom", "Erro ao abrir artigo: ${e.message}")
                showToast("Erro ao abrir artigo: ${e.message}")
            }
        }
    }

    private fun showToast(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_LONG).show()
    }
} 