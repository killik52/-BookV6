package database.dao

import androidx.room.*
import database.entities.Fatura
import kotlinx.coroutines.flow.Flow

@Dao
interface FaturaDao {
    @Query("SELECT * FROM faturas ORDER BY data DESC")
    fun getAllFaturas(): Flow<List<Fatura>>

    @Query("SELECT * FROM faturas WHERE id = :id")
    suspend fun getFaturaById(id: Long): Fatura?

    @Query("SELECT * FROM faturas WHERE numeroFatura LIKE '%' || :searchQuery || '%' OR cliente LIKE '%' || :searchQuery || '%'")
    fun searchFaturas(searchQuery: String): Flow<List<Fatura>>

    @Query("SELECT * FROM faturas WHERE foiEnviada = :foiEnviada")
    fun getFaturasByEnvioStatus(foiEnviada: Boolean): Flow<List<Fatura>>

    @Query("SELECT * FROM faturas WHERE data BETWEEN :startDate AND :endDate")
    fun getFaturasByDateRange(startDate: String, endDate: String): Flow<List<Fatura>>

    @Query("SELECT SUM(subtotal) FROM faturas WHERE data BETWEEN :startDate AND :endDate")
    suspend fun getTotalFaturasByDateRange(startDate: String, endDate: String): Double?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFatura(fatura: Fatura): Long

    @Update
    suspend fun updateFatura(fatura: Fatura)

    @Delete
    suspend fun deleteFatura(fatura: Fatura)

    @Query("DELETE FROM faturas WHERE id = :id")
    suspend fun deleteFaturaById(id: Long)

    @Query("SELECT COUNT(*) FROM faturas")
    suspend fun getFaturaCount(): Int

    @Query("SELECT COUNT(*) FROM faturas WHERE foiEnviada = 1")
    suspend fun getFaturasEnviadasCount(): Int

    @Query("SELECT COUNT(*) FROM faturas WHERE foiEnviada = 0")
    suspend fun getFaturasNaoEnviadasCount(): Int
} 