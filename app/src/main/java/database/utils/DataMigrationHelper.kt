package database.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import database.entities.Cliente
import database.entities.Artigo
import database.entities.Fatura
import database.repository.ClienteRepository
import database.repository.ArtigoRepository
import database.repository.FaturaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataMigrationHelper(private val context: Context) {
    
    companion object {
        private const val OLD_DATABASE_NAME = "myapplication.db"
        private const val OLD_DATABASE_VERSION = 19
    }
    
    // Classe interna para acessar o banco SQLite antigo
    private inner class OldDatabaseHelper(context: Context) : SQLiteOpenHelper(context, OLD_DATABASE_NAME, null, OLD_DATABASE_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            // Não precisamos criar o banco antigo, apenas acessá-lo
        }
        
        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            // Não precisamos fazer upgrade do banco antigo
        }
    }
    
    /**
     * Migra todos os dados do SQLite antigo para o Room
     */
    suspend fun migrateAllData(
        clienteRepository: ClienteRepository,
        artigoRepository: ArtigoRepository,
        faturaRepository: FaturaRepository,
        onProgress: (String) -> Unit = {}
    ) = withContext(Dispatchers.IO) {
        try {
            val oldDbHelper = OldDatabaseHelper(context)
            val oldDb = oldDbHelper.readableDatabase
            
            onProgress("Iniciando migração de dados...")
            
            // Migra clientes
            migrateClientes(oldDb, clienteRepository, onProgress)
            
            // Migra artigos
            migrateArtigos(oldDb, artigoRepository, onProgress)
            
            // Migra faturas
            migrateFaturas(oldDb, faturaRepository, onProgress)
            
            // Migra clientes bloqueados
            migrateClientesBloqueados(oldDb, onProgress)
            
            onProgress("Migração concluída com sucesso!")
            Log.i("DataMigrationHelper", "Migração de dados concluída com sucesso")
            
        } catch (e: Exception) {
            Log.e("DataMigrationHelper", "Erro durante a migração: ${e.message}", e)
            onProgress("Erro durante a migração: ${e.message}")
            throw e
        }
    }
    
    private suspend fun migrateClientes(
        oldDb: SQLiteDatabase,
        repository: ClienteRepository,
        onProgress: (String) -> Unit
    ) {
        onProgress("Migrando clientes...")
        
        val cursor = oldDb.query(
            "clientes", null, null, null, null, null, null
        )
        
        var count = 0
        cursor?.use {
            while (it.moveToNext()) {
                val cliente = Cliente(
                    id = it.getLong(it.getColumnIndexOrThrow(BaseColumns._ID)),
                    nome = it.getString(it.getColumnIndexOrThrow("nome")),
                    email = it.getString(it.getColumnIndexOrThrow("email")),
                    telefone = it.getString(it.getColumnIndexOrThrow("telefone")),
                    informacoesAdicionais = it.getString(it.getColumnIndexOrThrow("informacoes_adicionais")),
                    cpf = it.getString(it.getColumnIndexOrThrow("cpf")),
                    cnpj = it.getString(it.getColumnIndexOrThrow("cnpj")),
                    logradouro = it.getString(it.getColumnIndexOrThrow("logradouro")),
                    numero = it.getString(it.getColumnIndexOrThrow("numero")),
                    complemento = it.getString(it.getColumnIndexOrThrow("complemento")),
                    bairro = it.getString(it.getColumnIndexOrThrow("bairro")),
                    municipio = it.getString(it.getColumnIndexOrThrow("municipio")),
                    uf = it.getString(it.getColumnIndexOrThrow("uf")),
                    cep = it.getString(it.getColumnIndexOrThrow("cep")),
                    numeroSerial = it.getString(it.getColumnIndexOrThrow("numero_serial"))
                )
                
                repository.insertCliente(cliente)
                count++
            }
        }
        
        Log.i("DataMigrationHelper", "Migrados $count clientes")
        onProgress("Migrados $count clientes")
    }
    
    private suspend fun migrateArtigos(
        oldDb: SQLiteDatabase,
        repository: ArtigoRepository,
        onProgress: (String) -> Unit
    ) {
        onProgress("Migrando artigos...")
        
        val cursor = oldDb.query(
            "artigos", null, null, null, null, null, null
        )
        
        var count = 0
        cursor?.use {
            while (it.moveToNext()) {
                val artigo = Artigo(
                    id = it.getLong(it.getColumnIndexOrThrow(BaseColumns._ID)),
                    nome = it.getString(it.getColumnIndexOrThrow("nome")),
                    preco = it.getDouble(it.getColumnIndexOrThrow("preco")),
                    quantidade = it.getInt(it.getColumnIndexOrThrow("quantidade")),
                    desconto = it.getDouble(it.getColumnIndexOrThrow("desconto")),
                    descricao = it.getString(it.getColumnIndexOrThrow("descricao")),
                    guardarFatura = it.getInt(it.getColumnIndexOrThrow("guardar_fatura")) == 1,
                    numeroSerial = it.getString(it.getColumnIndexOrThrow("numero_serial"))
                )
                
                repository.insertArtigo(artigo)
                count++
            }
        }
        
        Log.i("DataMigrationHelper", "Migrados $count artigos")
        onProgress("Migrados $count artigos")
    }
    
    private suspend fun migrateFaturas(
        oldDb: SQLiteDatabase,
        repository: FaturaRepository,
        onProgress: (String) -> Unit
    ) {
        onProgress("Migrando faturas...")
        
        val cursor = oldDb.query(
            "faturas", null, null, null, null, null, null
        )
        
        var count = 0
        cursor?.use {
            while (it.moveToNext()) {
                val fatura = Fatura(
                    id = it.getLong(it.getColumnIndexOrThrow(BaseColumns._ID)),
                    numeroFatura = it.getString(it.getColumnIndexOrThrow("numero_fatura")),
                    cliente = it.getString(it.getColumnIndexOrThrow("cliente")),
                    artigos = it.getString(it.getColumnIndexOrThrow("artigos")),
                    subtotal = it.getDouble(it.getColumnIndexOrThrow("subtotal")),
                    desconto = it.getDouble(it.getColumnIndexOrThrow("desconto")),
                    descontoPercent = it.getInt(it.getColumnIndexOrThrow("desconto_percent")),
                    taxaEntrega = it.getDouble(it.getColumnIndexOrThrow("taxa_entrega")),
                    saldoDevedor = it.getDouble(it.getColumnIndexOrThrow("saldo_devedor")),
                    data = it.getString(it.getColumnIndexOrThrow("data")),
                    fotosImpressora = it.getString(it.getColumnIndexOrThrow("fotos_impressora")),
                    notas = it.getString(it.getColumnIndexOrThrow("notas")),
                    foiEnviada = it.getInt(it.getColumnIndexOrThrow("foi_enviada")) == 1
                )
                
                repository.insertFatura(fatura)
                count++
            }
        }
        
        Log.i("DataMigrationHelper", "Migradas $count faturas")
        onProgress("Migradas $count faturas")
    }
    
    private suspend fun migrateClientesBloqueados(
        oldDb: SQLiteDatabase,
        onProgress: (String) -> Unit
    ) {
        onProgress("Migrando clientes bloqueados...")
        
        val cursor = oldDb.query(
            "clientes_bloqueados", null, null, null, null, null, null
        )
        
        var count = 0
        cursor?.use {
            while (it.moveToNext()) {
                // Para clientes bloqueados, você pode criar uma entidade separada
                // ou adicionar um campo de status na entidade Cliente
                // Por enquanto, vamos apenas logar
                val nome = it.getString(it.getColumnIndexOrThrow("nome"))
                Log.d("DataMigrationHelper", "Cliente bloqueado encontrado: $nome")
                count++
            }
        }
        
        Log.i("DataMigrationHelper", "Encontrados $count clientes bloqueados")
        onProgress("Encontrados $count clientes bloqueados")
    }
    
    /**
     * Verifica se existe um banco SQLite antigo para migrar
     */
    fun hasOldDatabase(): Boolean {
        val dbFile = context.getDatabasePath(OLD_DATABASE_NAME)
        return dbFile.exists()
    }
    
    /**
     * Remove o banco SQLite antigo após migração bem-sucedida
     */
    fun removeOldDatabase() {
        try {
            val dbFile = context.getDatabasePath(OLD_DATABASE_NAME)
            if (dbFile.exists()) {
                dbFile.delete()
                Log.i("DataMigrationHelper", "Banco SQLite antigo removido com sucesso")
            }
        } catch (e: Exception) {
            Log.e("DataMigrationHelper", "Erro ao remover banco antigo: ${e.message}", e)
        }
    }
} 