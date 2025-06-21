package com.example.myapplication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import database.entities.Fatura
import database.entities.FaturaLixeira
import database.repository.FaturaRepository
import database.AppDatabase
import kotlinx.coroutines.launch

class FaturaViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: FaturaRepository
    val allFaturas: LiveData<List<Fatura>>
    
    init {
        val dao = AppDatabase.getDatabase(application).faturaDao()
        val lixeiraDao = AppDatabase.getDatabase(application).faturaLixeiraDao()
        repository = FaturaRepository(dao, lixeiraDao)
        allFaturas = repository.getAllFaturas()
    }
    
    // Função para buscar faturas
    fun searchFaturas(query: String): LiveData<List<Fatura>> = repository.searchFaturas(query)
    
    // Função para obter faturas por status de envio
    fun getFaturasByEnvioStatus(foiEnviada: Boolean): LiveData<List<Fatura>> = repository.getFaturasByEnvioStatus(foiEnviada)
    
    // Função para obter faturas por período
    fun getFaturasByDateRange(startDate: String, endDate: String): LiveData<List<Fatura>> = repository.getFaturasByDateRange(startDate, endDate)
    
    // Função para inserir fatura
    fun insertFatura(fatura: Fatura) = viewModelScope.launch {
        repository.insertFatura(fatura)
    }
    
    // Função para atualizar fatura
    fun updateFatura(fatura: Fatura) = viewModelScope.launch {
        repository.updateFatura(fatura)
    }
    
    // Função para deletar fatura
    fun deleteFatura(fatura: Fatura) = viewModelScope.launch {
        repository.deleteFatura(fatura)
    }
    
    // Função para obter fatura por ID
    fun getFaturaById(id: Long): LiveData<Fatura?> {
        return repository.getFaturaById(id)
    }
    
    // Função para inserir fatura na lixeira
    fun insertFaturaLixeira(faturaLixeira: FaturaLixeira) = viewModelScope.launch {
        repository.insertFaturaLixeira(faturaLixeira)
    }
    
    // Função para obter contagem de faturas
    fun getFaturaCount(): LiveData<Int> {
        return repository.getFaturaCount()
    }
    
    // Função para obter total de faturas por período
    fun getTotalFaturasByDateRange(startDate: String, endDate: String): LiveData<Double?> {
        return repository.getTotalFaturasByDateRange(startDate, endDate)
    }
    
    // Função para obter contagem de faturas enviadas
    fun getFaturasEnviadasCount(): LiveData<Int> {
        return repository.getFaturasEnviadasCount()
    }
    
    // Função para obter contagem de faturas não enviadas
    fun getFaturasNaoEnviadasCount(): LiveData<Int> {
        return repository.getFaturasNaoEnviadasCount()
    }
} 