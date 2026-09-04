package com.example.juan.buscachinos.domain.usecase

import com.example.juan.buscachinos.domain.model.GeoPoint
import com.example.juan.buscachinos.domain.repository.LocationRepository

class GetLastKnownLocationUseCase(private val repository: LocationRepository) {
    operator fun invoke(): GeoPoint? = repository.getLastKnownLocation()
}
