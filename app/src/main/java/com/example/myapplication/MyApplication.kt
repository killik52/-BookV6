package com.example.myapplication

import android.app.Application
import database.AppDatabase

class MyApplication : Application() {
    
    // Instância do banco de dados Room
    lateinit var database: AppDatabase
        private set
    
    override fun onCreate() {
        super.onCreate()
        
        // Inicializa o banco de dados Room
        database = AppDatabase.getDatabase(this)
    }
} 