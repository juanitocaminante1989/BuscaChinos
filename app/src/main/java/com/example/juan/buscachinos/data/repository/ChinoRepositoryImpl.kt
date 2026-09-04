package com.example.juan.buscachinos.data.repository

import com.example.juan.buscachinos.data.local.ChinoDao
import com.example.juan.buscachinos.data.local.ChinoEntity
import com.example.juan.buscachinos.domain.model.Chino
import com.example.juan.buscachinos.domain.model.GeoPoint
import com.example.juan.buscachinos.domain.repository.ChinoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChinoRepositoryImpl(private val dao: ChinoDao) : ChinoRepository {

    override fun observeChinos(): Flow<List<Chino>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun addChino(name: String, location: GeoPoint) {
        dao.insert(
            ChinoEntity(
                name = name,
                longitude = location.longitude,
                latitude = location.latitude
            )
        )
    }

    override suspend fun deleteChino(id: Long) {
        dao.deleteById(id)
    }

    private fun ChinoEntity.toDomain() = Chino(
        id = id,
        name = name,
        location = GeoPoint(latitude = latitude, longitude = longitude)
    )
}
