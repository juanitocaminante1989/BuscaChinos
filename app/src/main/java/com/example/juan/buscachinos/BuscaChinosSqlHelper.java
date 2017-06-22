package com.example.juan.buscachinos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Juan on 22/06/2017.
 */

public class BuscaChinosSqlHelper extends SQLiteOpenHelper {

    String sqlCreateChino = "CREATE TABLE chino(codChino INTEGER(1000),chino_name VARCHAR(50), longitud DOUBLE(100) NOT NULL , latitud  DOUBLE(100) NOT NULL, PRIMARY KEY (codChino))";

    public BuscaChinosSqlHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(sqlCreateChino);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS chino");
    }
}
