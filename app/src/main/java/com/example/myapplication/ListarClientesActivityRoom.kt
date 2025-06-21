package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
    private lateinit var editTextBusca: android.widget.EditText
    private lateinit var listaClientes: MutableList<Cliente>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_clientes)

        // Inicializa o ViewModel
        clienteViewModel = ViewModelProvider(this)[ClienteViewModel::class.java]

        // Configura a RecyclerView
        recyclerView = findViewById(R.id.recyclerViewClientes)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ClienteAdapter(this) { cliente ->
            abrirDetalhesCliente(cliente)
        }
        recyclerView.adapter = adapter

        // Adiciona espaçamento entre os itens
        recyclerView.addItemDecoration(VerticalSpaceItemDecoration(16))

        // Configura a busca
        editTextBusca = findViewById(R.id.editTextBusca)
        editTextBusca.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filtrarClientes(s.toString())
            }
        })

        // Carrega os clientes
        carregarClientes()
    }

    private fun carregarClientes() {
        clienteViewModel.getAllClientes().observe(this) { clientes ->
            listaClientes = clientes.toMutableList()
            adapter.submitList(listaClientes)
        }
    }

    private fun filtrarClientes(query: String) {
        if (query.isEmpty()) {
            adapter.submitList(listaClientes)
        } else {
            val clientesFiltrados = listaClientes.filter { cliente ->
                cliente.nome.contains(query, ignoreCase = true) ||
                cliente.email.contains(query, ignoreCase = true) ||
                cliente.telefone.contains(query, ignoreCase = true)
            }
            adapter.submitList(clientesFiltrados)
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
                filtrarClientes(searchQuery)
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
    private val context: android.content.Context,
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