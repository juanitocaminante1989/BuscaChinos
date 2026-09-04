package com.example.juan.buscachinos.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChinoDao {
    @Query("SELECT * FROM chino")
    fun observeAll(): Flow<List<ChinoEntity>>

    @Insert
    suspend fun insert(chino: ChinoEntity)

    @Query("DELETE FROM chino WHERE codChino = :id")
    suspend fun deleteById(id: Long)
}
