package com.example.juan.buscachinos.domain.repository

import com.example.juan.buscachinos.domain.model.Chino
import com.example.juan.buscachinos.domain.model.GeoPoint
import kotlinx.coroutines.flow.Flow

interface ChinoRepository {
    fun observeChinos(): Flow<List<Chino>>
    suspend fun addChino(name: String, location: GeoPoint)
    suspend fun deleteChino(id: Long)
}
