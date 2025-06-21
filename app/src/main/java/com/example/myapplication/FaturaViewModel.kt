package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import database.entities.Fatura
import database.repository.FaturaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FaturaViewModel(private val repository: FaturaRepository) : ViewModel() {
    
    // Flow para observar mudanças na lista de faturas
    val allFaturas: Flow<List<Fatura>> = repository.getAllFaturas()
    
    // Função para buscar faturas
    fun searchFaturas(query: String): Flow<List<Fatura>> = repository.searchFaturas(query)
    
    // Função para obter faturas por status de envio
    fun getFaturasByEnvioStatus(foiEnviada: Boolean): Flow<List<Fatura>> = repository.getFaturasByEnvioStatus(foiEnviada)
    
    // Função para obter faturas por período
    fun getFaturasByDateRange(startDate: String, endDate: String): Flow<List<Fatura>> = repository.getFaturasByDateRange(startDate, endDate)
    
    // Função para inserir fatura
    fun insertFatura(fatura: Fatura, onSuccess: (Long) -> Unit = {}, onError: (Exception) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val id = repository.insertFatura(fatura)
                onSuccess(id)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
    
    // Função para atualizar fatura
    fun updateFatura(fatura: Fatura, onSuccess: () -> Unit = {}, onError: (Exception) -> Unit = {}) {
        viewModelScope.launch {
            try {
                repository.updateFatura(fatura)
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
    
    // Função para deletar fatura
    fun deleteFatura(fatura: Fatura, onSuccess: () -> Unit = {}, onError: (Exception) -> Unit = {}) {
        viewModelScope.launch {
            try {
                repository.deleteFatura(fatura)
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
    
    // Função para obter fatura por ID
    fun getFaturaById(id: Long, onSuccess: (Fatura?) -> Unit = {}, onError: (Exception) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val fatura = repository.getFaturaById(id)
                onSuccess(fatura)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
    
    // Função para obter contagem de faturas
    fun getFaturaCount(onSuccess: (Int) -> Unit = {}, onError: (Exception) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val count = repository.getFaturaCount()
                onSuccess(count)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
    
    // Função para obter total de faturas por período
    fun getTotalFaturasByDateRange(startDate: String, endDate: String, onSuccess: (Double?) -> Unit = {}, onError: (Exception) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val total = repository.getTotalFaturasByDateRange(startDate, endDate)
                onSuccess(total)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
    
    // Função para obter contagem de faturas enviadas
    fun getFaturasEnviadasCount(onSuccess: (Int) -> Unit = {}, onError: (Exception) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val count = repository.getFaturasEnviadasCount()
                onSuccess(count)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
    
    // Função para obter contagem de faturas não enviadas
    fun getFaturasNaoEnviadasCount(onSuccess: (Int) -> Unit = {}, onError: (Exception) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val count = repository.getFaturasNaoEnviadasCount()
                onSuccess(count)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}

// Factory para criar o ViewModel com dependências
class FaturaViewModelFactory(private val repository: FaturaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FaturaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FaturaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 