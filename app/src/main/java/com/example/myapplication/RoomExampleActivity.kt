package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import database.AppDatabase
import database.entities.Cliente
import database.entities.Artigo
import database.entities.Fatura
import kotlinx.coroutines.launch

class RoomExampleActivity : AppCompatActivity() {
    
    private lateinit var database: AppDatabase
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Usando layout existente temporariamente
        
        // Inicializar o banco de dados Room
        database = AppDatabase.getDatabase(this)
        
        // Exemplo de uso do Room Database
        exemploUsoRoom()
    }
    
    private fun exemploUsoRoom() {
        lifecycleScope.launch {
            try {
                // Exemplo: Inserir um cliente
                val novoCliente = Cliente(
                    nome = "João Silva",
                    email = "joao@email.com",
                    telefone = "(11) 99999-9999",
                    informacoesAdicionais = "Cliente VIP",
                    cpf = "123.456.789-00",
                    cnpj = null,
                    logradouro = "Rua das Flores",
                    numero = "123",
                    complemento = "Apto 45",
                    bairro = "Centro",
                    municipio = "São Paulo",
                    uf = "SP",
                    cep = "01234-567",
                    numeroSerial = "CLI001"
                )
                
                val clienteId = database.clienteDao().insertCliente(novoCliente)
                Log.d("RoomExample", "Cliente inserido com ID: $clienteId")
                
                // Exemplo: Inserir um artigo
                val novoArtigo = Artigo(
                    nome = "Produto A",
                    preco = 29.99,
                    quantidade = 10,
                    desconto = 0.0,
                    descricao = "Descrição do produto A",
                    guardarFatura = true,
                    numeroSerial = "ART001"
                )
                
                val artigoId = database.artigoDao().insertArtigo(novoArtigo)
                Log.d("RoomExample", "Artigo inserido com ID: $artigoId")
                
                // Exemplo: Inserir uma fatura
                val novaFatura = Fatura(
                    numeroFatura = "FAT001",
                    cliente = "João Silva",
                    artigos = "Produto A",
                    subtotal = 299.90,
                    desconto = 0.0,
                    descontoPercent = 0,
                    taxaEntrega = 10.0,
                    saldoDevedor = 309.90,
                    data = "2024-01-15",
                    fotosImpressora = null,
                    notas = "Fatura de exemplo",
                    foiEnviada = false
                )
                
                val faturaId = database.faturaDao().insertFatura(novaFatura)
                Log.d("RoomExample", "Fatura inserida com ID: $faturaId")
                
                // Exemplo: Buscar todos os clientes
                database.clienteDao().getAllClientes().collect { clientes ->
                    Log.d("RoomExample", "Total de clientes: ${clientes.size}")
                    clientes.forEach { cliente ->
                        Log.d("RoomExample", "Cliente: ${cliente.nome} - ${cliente.email}")
                    }
                }
                
                // Exemplo: Buscar todos os artigos
                database.artigoDao().getAllArtigos().collect { artigos ->
                    Log.d("RoomExample", "Total de artigos: ${artigos.size}")
                    artigos.forEach { artigo ->
                        Log.d("RoomExample", "Artigo: ${artigo.nome} - R$ ${artigo.preco}")
                    }
                }
                
                // Exemplo: Buscar todas as faturas
                database.faturaDao().getAllFaturas().collect { faturas ->
                    Log.d("RoomExample", "Total de faturas: ${faturas.size}")
                    faturas.forEach { fatura ->
                        Log.d("RoomExample", "Fatura: ${fatura.numeroFatura} - R$ ${fatura.subtotal}")
                    }
                }
                
            } catch (e: Exception) {
                Log.e("RoomExample", "Erro ao usar Room Database", e)
            }
        }
    }

    private fun carregarClientes() {
        clienteViewModel.getAllClientes().observe(this) { clientes ->
            clientes?.let { listaClientes ->
                val nomes = listaClientes.map { it.nome }
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, nomes)
                listViewClientes.adapter = adapter
                Log.d("RoomExampleActivity", "Carregados ${listaClientes.size} clientes")
            }
        }
    }

    private fun carregarArtigos() {
        artigoViewModel.getAllArtigos().observe(this) { artigos ->
            artigos?.let { listaArtigos ->
                val nomes = listaArtigos.map { it.nome }
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, nomes)
                listViewArtigos.adapter = adapter
                Log.d("RoomExampleActivity", "Carregados ${listaArtigos.size} artigos")
            }
        }
    }
} 