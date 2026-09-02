package com.example.juan.buscachinos

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by Juan on 22/06/2017.
 */
class BuscaChinosSqlHelper(
    context: Context?,
    name: String?,
    factory: CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {
    var sqlCreateChino: String =
        "CREATE TABLE chino(codChino INTEGER(1000),chino_name VARCHAR(50), longitud DOUBLE(100) NOT NULL , latitud  DOUBLE(100) NOT NULL, PRIMARY KEY (codChino))"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(sqlCreateChino)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS chino")
    }
}
