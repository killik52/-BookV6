package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import database.entities.Cliente
import database.repository.ClienteRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ListarClientesActivityRoom : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ClienteAdapter
    private lateinit var clienteViewModel: ClienteViewModel
    private var searchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_clientes)

        // Inicializa o ViewModel
        val repository = ClienteRepository((application as MyApplication).database.clienteDao())
        val factory = ClienteViewModelFactory(repository)
        clienteViewModel = ViewModelProvider(this, factory)[ClienteViewModel::class.java]

        // Configura o RecyclerView
        recyclerView = findViewById(R.id.listViewClientes)
        adapter = ClienteAdapter { cliente ->
            // Callback quando um cliente é clicado
            abrirDetalhesCliente(cliente)
        }
        
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Observa mudanças na lista de clientes
        observeClientes()
    }

    private fun observeClientes() {
        lifecycleScope.launch {
            if (searchQuery.isEmpty()) {
                // Observa todos os clientes
                clienteViewModel.allClientes.collectLatest { clientes ->
                    adapter.submitList(clientes)
                    Log.d("ListarClientesActivityRoom", "Carregados ${clientes.size} clientes")
                }
            } else {
                // Observa resultados da busca
                clienteViewModel.searchClientes(searchQuery).collectLatest { clientes ->
                    adapter.submitList(clientes)
                    Log.d("ListarClientesActivityRoom", "Encontrados ${clientes.size} clientes para '$searchQuery'")
                }
            }
        }
    }

    private fun abrirDetalhesCliente(cliente: Cliente) {
        val intent = Intent(this, ClienteActivityRoom::class.java).apply {
            putExtra("id", cliente.id)
        }
        startActivityForResult(intent, REQUEST_EDIT_CLIENTE)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchQuery = newText ?: ""
                // Reobserva os clientes com a nova query
                observeClientes()
                return true
            }
        })
        
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == REQUEST_EDIT_CLIENTE && resultCode == RESULT_OK) {
            // Cliente foi editado, a lista será atualizada automaticamente pelo Flow
            Log.d("ListarClientesActivityRoom", "Cliente editado, lista atualizada")
        }
    }

    companion object {
        private const val REQUEST_EDIT_CLIENTE = 1001
    }
}

// Adapter para o RecyclerView usando ListAdapter
class ClienteAdapter(
    private val onClienteClick: (Cliente) -> Unit
) : androidx.recyclerview.widget.ListAdapter<Cliente, ClienteAdapter.ClienteViewHolder>(ClienteDiffCallback()) {

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ClienteViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cliente_lista, parent, false)
        return ClienteViewHolder(view, onClienteClick)
    }

    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ClienteViewHolder(
        itemView: View,
        private val onClienteClick: (Cliente) -> Unit
    ) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        
        private val textView: android.widget.TextView = itemView.findViewById(R.id.text1)

        fun bind(cliente: Cliente) {
            val displayText = buildString {
                append(cliente.nome ?: "Nome não informado")
                append("\n")
                append(cliente.email ?: "Email não informado")
                append(" | ")
                append(cliente.telefone ?: "Telefone não informado")
            }
            
            textView.text = displayText
            
            itemView.setOnClickListener {
                onClienteClick(cliente)
            }
        }
    }
}

// DiffCallback para otimizar atualizações do RecyclerView
class ClienteDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<Cliente>() {
    override fun areItemsTheSame(oldItem: Cliente, newItem: Cliente): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Cliente, newItem: Cliente): Boolean {
        return oldItem == newItem
    }
} 