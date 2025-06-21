package com.example.myapplication

import android.provider.BaseColumns

object FaturaLixeiraContract {
    object FaturaLixeiraEntry : BaseColumns {
        const val TABLE_NAME = "faturas_lixeira"
        const val COLUMN_NAME_NUMERO_FATURA = "numero_fatura"
        const val COLUMN_NAME_CLIENTE = "cliente"
        const val COLUMN_NAME_ARTIGOS = "artigos"
        const val COLUMN_NAME_SUBTOTAL = "subtotal"
        const val COLUMN_NAME_DESCONTO = "desconto"
        const val COLUMN_NAME_DESCONTO_PERCENT = "desconto_percent"
        const val COLUMN_NAME_TAXA_ENTREGA = "taxa_entrega"
        const val COLUMN_NAME_SALDO_DEVEDOR = "saldo_devedor"
        const val COLUMN_NAME_DATA = "data"
        const val COLUMN_NAME_FOTO_IMPRESSORA = "foto_impressora"
        const val COLUMN_NAME_NOTAS = "notas"

        const val SQL_CREATE_ENTRIES =
            "CREATE TABLE $TABLE_NAME (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COLUMN_NAME_NUMERO_FATURA TEXT," +
                    "$COLUMN_NAME_CLIENTE TEXT," +
                    "$COLUMN_NAME_ARTIGOS TEXT," +
                    "$COLUMN_NAME_SUBTOTAL REAL," +
                    "$COLUMN_NAME_DESCONTO REAL," +
                    "$COLUMN_NAME_DESCONTO_PERCENT INTEGER," +
                    "$COLUMN_NAME_TAXA_ENTREGA REAL," +
                    "$COLUMN_NAME_SALDO_DEVEDOR REAL," +
                    "$COLUMN_NAME_DATA TEXT," +
                    "$COLUMN_NAME_FOTO_IMPRESSORA TEXT," +
                    "$COLUMN_NAME_NOTAS TEXT)"

        const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"
    }
} 