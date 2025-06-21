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
import kotlinx.coroutines.flow.collect

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
                
            } catch (e: Exception) {
                Log.e("RoomExample", "Erro ao usar Room Database", e)
            }
        }
        
        // Observações movidas para fora do lifecycleScope
        database.clienteDao().getAllClientes().observe(this) { clientes: List<Cliente> ->
            Log.d("RoomExample", "Total de clientes: ${clientes.size}")
            clientes.forEach { cliente: Cliente ->
                Log.d("RoomExample", "Cliente: ${cliente.nome} - ${cliente.email}")
            }
        }
        
        lifecycleScope.launch {
            database.artigoDao().getAllArtigos().collect { artigos: List<Artigo> ->
                Log.d("RoomExample", "Total de artigos: ${artigos.size}")
                artigos.forEach { artigo: Artigo ->
                    Log.d("RoomExample", "Artigo: ${artigo.nome} - R$ ${artigo.preco}")
                }
            }
        }
        
        database.faturaDao().getAllFaturas().observe(this) { faturas: List<Fatura> ->
            Log.d("RoomExample", "Total de faturas: ${faturas.size}")
            faturas.forEach { fatura: Fatura ->
                Log.d("RoomExample", "Fatura: ${fatura.numeroFatura} - R$ ${fatura.subtotal}")
            }
        }
    }

    private fun carregarClientes() {
        // TODO: Implementar quando ClienteViewModel estiver completo
        Log.d("RoomExampleActivity", "Carregamento de clientes será implementado")
    }

    private fun carregarArtigos() {
        // TODO: Implementar quando ArtigoViewModel estiver completo
        Log.d("RoomExampleActivity", "Carregamento de artigos será implementado")
    }
} 