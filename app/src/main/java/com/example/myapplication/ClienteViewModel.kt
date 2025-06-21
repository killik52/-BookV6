package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import database.entities.Cliente
import database.repository.ClienteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ClienteViewModel(private val repository: ClienteRepository) : ViewModel() {
    
    // Flow para observar mudanças na lista de clientes
    val allClientes: Flow<List<Cliente>> = repository.getAllClientes()
    
    // Função para buscar clientes
    fun searchClientes(query: String): Flow<List<Cliente>> = repository.searchClientes(query)
    
    // Função para obter clientes recentes
    fun getRecentClientes(limit: Int): Flow<List<Cliente>> = repository.getRecentClientes(limit)
    
    // Função para inserir cliente
    fun insertCliente(cliente: Cliente, onSuccess: (Long) -> Unit = {}, onError: (Exception) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val id = repository.insertCliente(cliente)
                onSuccess(id)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
    
    // Função para atualizar cliente
    fun updateCliente(cliente: Cliente, onSuccess: () -> Unit = {}, onError: (Exception) -> Unit = {}) {
        viewModelScope.launch {
            try {
                repository.updateCliente(cliente)
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
    
    // Função para deletar cliente
    fun deleteCliente(cliente: Cliente, onSuccess: () -> Unit = {}, onError: (Exception) -> Unit = {}) {
        viewModelScope.launch {
            try {
                repository.deleteCliente(cliente)
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
    
    // Função para obter cliente por ID
    fun getClienteById(id: Long, onSuccess: (Cliente?) -> Unit = {}, onError: (Exception) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val cliente = repository.getClienteById(id)
                onSuccess(cliente)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
    
    // Função para obter cliente por número serial
    fun getClienteByNumeroSerial(numeroSerial: String, onSuccess: (Cliente?) -> Unit = {}, onError: (Exception) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val cliente = repository.getClienteByNumeroSerial(numeroSerial)
                onSuccess(cliente)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
    
    // Função para obter contagem de clientes
    fun getClienteCount(onSuccess: (Int) -> Unit = {}, onError: (Exception) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val count = repository.getClienteCount()
                onSuccess(count)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}

// Factory para criar o ViewModel com dependências
class ClienteViewModelFactory(private val repository: ClienteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClienteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClienteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 