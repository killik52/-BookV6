package database.repository

import database.dao.ClienteDao
import database.entities.Cliente
import kotlinx.coroutines.flow.Flow

class ClienteRepository(
    private val clienteDao: ClienteDao
) {
    fun getAllClientes(): Flow<List<Cliente>> = clienteDao.getAllClientes()
    
    suspend fun getClienteById(id: Long): Cliente? = clienteDao.getClienteById(id)
    
    fun searchClientes(searchQuery: String): Flow<List<Cliente>> = clienteDao.searchClientes(searchQuery)
    
    suspend fun getClienteByNumeroSerial(numeroSerial: String): Cliente? = clienteDao.getClienteByNumeroSerial(numeroSerial)
    
    suspend fun insertCliente(cliente: Cliente): Long = clienteDao.insertCliente(cliente)
    
    suspend fun updateCliente(cliente: Cliente) = clienteDao.updateCliente(cliente)
    
    suspend fun deleteCliente(cliente: Cliente) = clienteDao.deleteCliente(cliente)
    
    suspend fun deleteClienteById(id: Long) = clienteDao.deleteClienteById(id)
    
    suspend fun getClienteCount(): Int = clienteDao.getClienteCount()
    
    fun getRecentClientes(limit: Int): Flow<List<Cliente>> = clienteDao.getRecentClientes(limit)
} 