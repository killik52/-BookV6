package database.utils

import android.content.Context
import android.database.Cursor
import android.util.Log
import com.example.myapplication.ClienteDbHelper
import database.AppDatabase
import database.entities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataMigrationHelper(private val context: Context) {
    
    private val oldDbHelper = ClienteDbHelper(context)
    private val roomDb = AppDatabase.getDatabase(context)
    
    suspend fun migrateAllData(): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d("DataMigration", "Iniciando migração de dados...")
            
            val oldDb = oldDbHelper.readableDatabase
            
            // Migrar clientes
            migrateClientes(oldDb)
            
            // Migrar artigos
            migrateArtigos(oldDb)
            
            // Migrar faturas
            migrateFaturas(oldDb)
            
            // Migrar itens de fatura
            migrateFaturaItens(oldDb)
            
            // Migrar notas de fatura
            migrateFaturaNotas(oldDb)
            
            // Migrar fotos de fatura
            migrateFaturaFotos(oldDb)
            
            // Migrar clientes bloqueados
            migrateClientesBloqueados(oldDb)
            
            // Migrar faturas da lixeira
            migrateFaturasLixeira(oldDb)
            
            Log.d("DataMigration", "Migração concluída com sucesso!")
            true
            
        } catch (e: Exception) {
            Log.e("DataMigration", "Erro durante a migração", e)
            false
        } finally {
            oldDbHelper.close()
        }
    }
    
    private suspend fun migrateClientes(oldDb: android.database.sqlite.SQLiteDatabase) {
        Log.d("DataMigration", "Migrando clientes...")
        
        val cursor = oldDb.query("clientes", null, null, null, null, null, null)
        cursor?.use {
            var count = 0
            while (it.moveToNext()) {
                val cliente = Cliente(
                    id = it.getLong(it.getColumnIndexOrThrow("_id")),
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
                
                roomDb.clienteDao().insertCliente(cliente)
                count++
            }
            Log.d("DataMigration", "Migrados $count clientes")
        }
    }
    
    private suspend fun migrateArtigos(oldDb: android.database.sqlite.SQLiteDatabase) {
        Log.d("DataMigration", "Migrando artigos...")
        
        val cursor = oldDb.query("artigos", null, null, null, null, null, null)
        cursor?.use {
            var count = 0
            while (it.moveToNext()) {
                val artigo = Artigo(
                    id = it.getLong(it.getColumnIndexOrThrow("_id")),
                    nome = it.getString(it.getColumnIndexOrThrow("nome")),
                    preco = it.getDouble(it.getColumnIndexOrThrow("preco")),
                    quantidade = it.getInt(it.getColumnIndexOrThrow("quantidade")),
                    desconto = it.getDouble(it.getColumnIndexOrThrow("desconto")),
                    descricao = it.getString(it.getColumnIndexOrThrow("descricao")),
                    guardarFatura = it.getInt(it.getColumnIndexOrThrow("guardar_fatura")) == 1,
                    numeroSerial = it.getString(it.getColumnIndexOrThrow("numero_serial"))
                )
                
                roomDb.artigoDao().insertArtigo(artigo)
                count++
            }
            Log.d("DataMigration", "Migrados $count artigos")
        }
    }
    
    private suspend fun migrateFaturas(oldDb: android.database.sqlite.SQLiteDatabase) {
        Log.d("DataMigration", "Migrando faturas...")
        
        val cursor = oldDb.query("faturas", null, null, null, null, null, null)
        cursor?.use {
            var count = 0
            while (it.moveToNext()) {
                val fatura = Fatura(
                    id = it.getLong(it.getColumnIndexOrThrow("_id")),
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
                
                roomDb.faturaDao().insertFatura(fatura)
                count++
            }
            Log.d("DataMigration", "Migradas $count faturas")
        }
    }
    
    private suspend fun migrateFaturaItens(oldDb: android.database.sqlite.SQLiteDatabase) {
        Log.d("DataMigration", "Migrando itens de fatura...")
        
        val cursor = oldDb.query("fatura_itens", null, null, null, null, null, null)
        cursor?.use {
            var count = 0
            while (it.moveToNext()) {
                val faturaItem = FaturaItem(
                    id = it.getLong(it.getColumnIndexOrThrow("_id")),
                    faturaId = it.getLong(it.getColumnIndexOrThrow("fatura_id")),
                    artigoId = it.getLong(it.getColumnIndexOrThrow("artigo_id")),
                    quantidade = it.getInt(it.getColumnIndexOrThrow("quantidade")),
                    preco = it.getDouble(it.getColumnIndexOrThrow("preco")),
                    clienteId = if (it.getColumnIndex("cliente_id") != -1) {
                        it.getLong(it.getColumnIndex("cliente_id"))
                    } else null
                )
                
                roomDb.faturaItemDao().insertFaturaItem(faturaItem)
                count++
            }
            Log.d("DataMigration", "Migrados $count itens de fatura")
        }
    }
    
    private suspend fun migrateFaturaNotas(oldDb: android.database.sqlite.SQLiteDatabase) {
        Log.d("DataMigration", "Migrando notas de fatura...")
        
        val cursor = oldDb.query("fatura_notas", null, null, null, null, null, null)
        cursor?.use {
            var count = 0
            while (it.moveToNext()) {
                val faturaNota = FaturaNota(
                    id = it.getLong(it.getColumnIndexOrThrow("_id")),
                    faturaId = it.getLong(it.getColumnIndexOrThrow("fatura_id")),
                    nota = it.getString(it.getColumnIndexOrThrow("nota"))
                )
                
                roomDb.faturaNotaDao().insertFaturaNota(faturaNota)
                count++
            }
            Log.d("DataMigration", "Migradas $count notas de fatura")
        }
    }
    
    private suspend fun migrateFaturaFotos(oldDb: android.database.sqlite.SQLiteDatabase) {
        Log.d("DataMigration", "Migrando fotos de fatura...")
        
        val cursor = oldDb.query("fatura_fotos", null, null, null, null, null, null)
        cursor?.use {
            var count = 0
            while (it.moveToNext()) {
                val faturaFoto = FaturaFoto(
                    id = it.getLong(it.getColumnIndexOrThrow("_id")),
                    faturaId = it.getLong(it.getColumnIndexOrThrow("fatura_id")),
                    photoPath = it.getString(it.getColumnIndexOrThrow("photo_path"))
                )
                
                roomDb.faturaFotoDao().insertFaturaFoto(faturaFoto)
                count++
            }
            Log.d("DataMigration", "Migradas $count fotos de fatura")
        }
    }
    
    private suspend fun migrateClientesBloqueados(oldDb: android.database.sqlite.SQLiteDatabase) {
        Log.d("DataMigration", "Migrando clientes bloqueados...")
        
        val cursor = oldDb.query("clientes_bloqueados", null, null, null, null, null, null)
        cursor?.use {
            var count = 0
            while (it.moveToNext()) {
                val clienteBloqueado = ClienteBloqueado(
                    id = it.getLong(it.getColumnIndexOrThrow("_id")),
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
                
                roomDb.clienteBloqueadoDao().insertClienteBloqueado(clienteBloqueado)
                count++
            }
            Log.d("DataMigration", "Migrados $count clientes bloqueados")
        }
    }
    
    private suspend fun migrateFaturasLixeira(oldDb: android.database.sqlite.SQLiteDatabase) {
        Log.d("DataMigration", "Migrando faturas da lixeira...")
        
        val cursor = oldDb.query("faturas_lixeira", null, null, null, null, null, null)
        cursor?.use {
            var count = 0
            while (it.moveToNext()) {
                val faturaLixeira = FaturaLixeira(
                    id = it.getLong(it.getColumnIndexOrThrow("_id")),
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
                    notas = it.getString(it.getColumnIndexOrThrow("notas"))
                )
                
                roomDb.faturaLixeiraDao().insertFaturaLixeira(faturaLixeira)
                count++
            }
            Log.d("DataMigration", "Migradas $count faturas da lixeira")
        }
    }
} 