package database.repository

import androidx.lifecycle.LiveData
import database.dao.FaturaDao
import database.dao.FaturaLixeiraDao
import database.entities.Fatura
import database.entities.FaturaLixeira

class FaturaRepository(
    private val faturaDao: FaturaDao,
    private val faturaLixeiraDao: FaturaLixeiraDao? = null
) {
    fun getAllFaturas(): LiveData<List<Fatura>> = faturaDao.getAllFaturas()
    
    fun getFaturaById(id: Long): LiveData<Fatura?> = faturaDao.getFaturaById(id)
    
    fun searchFaturas(searchQuery: String): LiveData<List<Fatura>> = faturaDao.searchFaturas(searchQuery)
    
    fun getFaturasByEnvioStatus(foiEnviada: Boolean): LiveData<List<Fatura>> = faturaDao.getFaturasByEnvioStatus(foiEnviada)
    
    fun getFaturasByDateRange(startDate: String, endDate: String): LiveData<List<Fatura>> = faturaDao.getFaturasByDateRange(startDate, endDate)
    
    fun getTotalFaturasByDateRange(startDate: String, endDate: String): LiveData<Double?> = faturaDao.getTotalFaturasByDateRange(startDate, endDate)
    
    suspend fun insertFatura(fatura: Fatura): Long = faturaDao.insertFatura(fatura)
    
    suspend fun updateFatura(fatura: Fatura) = faturaDao.updateFatura(fatura)
    
    suspend fun deleteFatura(fatura: Fatura) = faturaDao.deleteFatura(fatura)
    
    suspend fun deleteFaturaById(id: Long) = faturaDao.deleteFaturaById(id)
    
    fun getFaturaCount(): LiveData<Int> = faturaDao.getFaturaCount()
    
    fun getFaturasEnviadasCount(): LiveData<Int> = faturaDao.getFaturasEnviadasCount()
    
    fun getFaturasNaoEnviadasCount(): LiveData<Int> = faturaDao.getFaturasNaoEnviadasCount()
    
    suspend fun insertFaturaLixeira(faturaLixeira: FaturaLixeira): Long {
        return faturaLixeiraDao?.insertFaturaLixeira(faturaLixeira) ?: -1L
    }
} 