package database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import database.entities.Fatura

@Dao
interface FaturaDao {
    @Query("SELECT * FROM faturas ORDER BY data DESC")
    fun getAllFaturas(): LiveData<List<Fatura>>

    @Query("SELECT * FROM faturas WHERE id = :id")
    fun getFaturaById(id: Long): LiveData<Fatura?>

    @Query("SELECT * FROM faturas WHERE numeroFatura LIKE '%' || :searchQuery || '%' OR cliente LIKE '%' || :searchQuery || '%'")
    fun searchFaturas(searchQuery: String): LiveData<List<Fatura>>

    @Query("SELECT * FROM faturas WHERE foiEnviada = :foiEnviada")
    fun getFaturasByEnvioStatus(foiEnviada: Boolean): LiveData<List<Fatura>>

    @Query("SELECT * FROM faturas WHERE data BETWEEN :startDate AND :endDate")
    fun getFaturasByDateRange(startDate: String, endDate: String): LiveData<List<Fatura>>

    @Query("SELECT SUM(subtotal) FROM faturas WHERE data BETWEEN :startDate AND :endDate")
    fun getTotalFaturasByDateRange(startDate: String, endDate: String): LiveData<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFatura(fatura: Fatura): Long

    @Update
    suspend fun updateFatura(fatura: Fatura)

    @Delete
    suspend fun deleteFatura(fatura: Fatura)

    @Query("DELETE FROM faturas WHERE id = :id")
    suspend fun deleteFaturaById(id: Long)

    @Query("SELECT COUNT(*) FROM faturas")
    fun getFaturaCount(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM faturas WHERE foiEnviada = 1")
    fun getFaturasEnviadasCount(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM faturas WHERE foiEnviada = 0")
    fun getFaturasNaoEnviadasCount(): LiveData<Int>
} 