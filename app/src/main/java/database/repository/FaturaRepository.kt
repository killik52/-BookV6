package database.repository

import database.dao.FaturaDao
import database.entities.Fatura
import kotlinx.coroutines.flow.Flow

class FaturaRepository(
    private val faturaDao: FaturaDao
) {
    fun getAllFaturas(): Flow<List<Fatura>> = faturaDao.getAllFaturas()
    
    suspend fun getFaturaById(id: Long): Fatura? = faturaDao.getFaturaById(id)
    
    fun searchFaturas(searchQuery: String): Flow<List<Fatura>> = faturaDao.searchFaturas(searchQuery)
    
    fun getFaturasByEnvioStatus(foiEnviada: Boolean): Flow<List<Fatura>> = faturaDao.getFaturasByEnvioStatus(foiEnviada)
    
    fun getFaturasByDateRange(startDate: String, endDate: String): Flow<List<Fatura>> = faturaDao.getFaturasByDateRange(startDate, endDate)
    
    suspend fun getTotalFaturasByDateRange(startDate: String, endDate: String): Double? = faturaDao.getTotalFaturasByDateRange(startDate, endDate)
    
    suspend fun insertFatura(fatura: Fatura): Long = faturaDao.insertFatura(fatura)
    
    suspend fun updateFatura(fatura: Fatura) = faturaDao.updateFatura(fatura)
    
    suspend fun deleteFatura(fatura: Fatura) = faturaDao.deleteFatura(fatura)
    
    suspend fun deleteFaturaById(id: Long) = faturaDao.deleteFaturaById(id)
    
    suspend fun getFaturaCount(): Int = faturaDao.getFaturaCount()
    
    suspend fun getFaturasEnviadasCount(): Int = faturaDao.getFaturasEnviadasCount()
    
    suspend fun getFaturasNaoEnviadasCount(): Int = faturaDao.getFaturasNaoEnviadasCount()
} 