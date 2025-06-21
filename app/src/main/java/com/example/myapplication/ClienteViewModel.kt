package com.example.myapplication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import database.entities.Cliente
import database.repository.ClienteRepository
import database.AppDatabase
import kotlinx.coroutines.launch

class ClienteViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: ClienteRepository
    val allClientes: LiveData<List<Cliente>>
    
    init {
        val dao = AppDatabase.getDatabase(application).clienteDao()
        repository = ClienteRepository(dao)
        allClientes = repository.getAllClientes()
    }
    
    // Função para buscar clientes
    fun searchClientes(query: String): LiveData<List<Cliente>> = repository.searchClientes(query)
    
    // Função para obter clientes recentes
    fun getRecentClientes(limit: Int): LiveData<List<Cliente>> = repository.getRecentClientes(limit)
    
    // Função para inserir cliente
    fun insertCliente(cliente: Cliente) = viewModelScope.launch {
        repository.insertCliente(cliente)
    }
    
    // Função para atualizar cliente
    fun updateCliente(cliente: Cliente) = viewModelScope.launch {
        repository.updateCliente(cliente)
    }
    
    // Função para deletar cliente
    fun deleteCliente(cliente: Cliente) = viewModelScope.launch {
        repository.deleteCliente(cliente)
    }
    
    // Função para obter cliente por ID
    fun getClienteById(id: Long): LiveData<Cliente?> {
        return repository.getClienteById(id)
    }
    
    // Função para obter cliente por número serial
    fun getClienteByNumeroSerial(numeroSerial: String): LiveData<Cliente?> {
        return repository.getClienteByNumeroSerial(numeroSerial)
    }
    
    // Função para obter contagem de clientes
    fun getClienteCount(): LiveData<Int> {
        return repository.getClienteCount()
    }
} 