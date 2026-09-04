package com.example.juan.buscachinos.domain.repository

import com.example.juan.buscachinos.domain.model.GeoPoint

interface LocationRepository {
    fun getLastKnownLocation(): GeoPoint?
}
