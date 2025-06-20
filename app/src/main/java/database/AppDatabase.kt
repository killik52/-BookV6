package database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import database.dao.*
import database.entities.*

@Database(
    entities = [
        Cliente::class,
        Artigo::class,
        Fatura::class,
        FaturaItem::class,
        FaturaNota::class,
        FaturaFoto::class,
        ClienteBloqueado::class,
        FaturaLixeira::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun clienteDao(): ClienteDao
    abstract fun artigoDao(): ArtigoDao
    abstract fun faturaDao(): FaturaDao
    abstract fun faturaItemDao(): FaturaItemDao
    abstract fun faturaNotaDao(): FaturaNotaDao
    abstract fun faturaFotoDao(): FaturaFotoDao
    abstract fun clienteBloqueadoDao(): ClienteBloqueadoDao
    abstract fun faturaLixeiraDao(): FaturaLixeiraDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "myapplication_room.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 