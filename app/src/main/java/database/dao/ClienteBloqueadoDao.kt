package database.dao

import androidx.room.*
import database.entities.ClienteBloqueado
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteBloqueadoDao {
    @Query("SELECT * FROM clientes_bloqueados ORDER BY nome ASC")
    fun getAllClientesBloqueados(): Flow<List<ClienteBloqueado>>

    @Query("SELECT * FROM clientes_bloqueados WHERE id = :id")
    suspend fun getClienteBloqueadoById(id: Long): ClienteBloqueado?

    @Query("SELECT * FROM clientes_bloqueados WHERE nome LIKE '%' || :searchQuery || '%' OR email LIKE '%' || :searchQuery || '%' OR telefone LIKE '%' || :searchQuery || '%'")
    fun searchClientesBloqueados(searchQuery: String): Flow<List<ClienteBloqueado>>

    @Query("SELECT * FROM clientes_bloqueados WHERE numeroSerial = :numeroSerial")
    suspend fun getClienteBloqueadoByNumeroSerial(numeroSerial: String): ClienteBloqueado?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClienteBloqueado(clienteBloqueado: ClienteBloqueado): Long

    @Update
    suspend fun updateClienteBloqueado(clienteBloqueado: ClienteBloqueado)

    @Delete
    suspend fun deleteClienteBloqueado(clienteBloqueado: ClienteBloqueado)

    @Query("DELETE FROM clientes_bloqueados WHERE id = :id")
    suspend fun deleteClienteBloqueadoById(id: Long)

    @Query("SELECT COUNT(*) FROM clientes_bloqueados")
    suspend fun getClienteBloqueadoCount(): Int
} 