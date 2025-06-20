package database.dao

import androidx.room.*
import database.entities.Cliente
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {
    @Query("SELECT * FROM clientes ORDER BY nome ASC")
    fun getAllClientes(): Flow<List<Cliente>>

    @Query("SELECT * FROM clientes WHERE id = :id")
    suspend fun getClienteById(id: Long): Cliente?

    @Query("SELECT * FROM clientes WHERE nome LIKE '%' || :searchQuery || '%' OR email LIKE '%' || :searchQuery || '%' OR telefone LIKE '%' || :searchQuery || '%'")
    fun searchClientes(searchQuery: String): Flow<List<Cliente>>

    @Query("SELECT * FROM clientes WHERE numeroSerial = :numeroSerial")
    suspend fun getClienteByNumeroSerial(numeroSerial: String): Cliente?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCliente(cliente: Cliente): Long

    @Update
    suspend fun updateCliente(cliente: Cliente)

    @Delete
    suspend fun deleteCliente(cliente: Cliente)

    @Query("DELETE FROM clientes WHERE id = :id")
    suspend fun deleteClienteById(id: Long)

    @Query("SELECT COUNT(*) FROM clientes")
    suspend fun getClienteCount(): Int

    @Query("SELECT * FROM clientes ORDER BY id DESC LIMIT :limit")
    fun getRecentClientes(limit: Int): Flow<List<Cliente>>
} 