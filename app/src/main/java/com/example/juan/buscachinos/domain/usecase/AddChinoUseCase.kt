package com.example.juan.buscachinos.domain.usecase

import com.example.juan.buscachinos.domain.model.GeoPoint
import com.example.juan.buscachinos.domain.repository.ChinoRepository

class AddChinoUseCase(private val repository: ChinoRepository) {
    suspend operator fun invoke(name: String, location: GeoPoint) {
        repository.addChino(name, location)
    }
}
