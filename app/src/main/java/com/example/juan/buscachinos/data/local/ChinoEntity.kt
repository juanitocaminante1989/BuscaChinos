package com.example.juan.buscachinos.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chino")
data class ChinoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "codChino")
    val id: Long = 0,
    @ColumnInfo(name = "chino_name")
    val name: String,
    @ColumnInfo(name = "longitud")
    val longitude: Double,
    @ColumnInfo(name = "latitud")
    val latitude: Double
)
