package com.example.juan.buscachinos.domain.model

data class Chino(
    val id: Long,
    val name: String,
    val location: GeoPoint
)
