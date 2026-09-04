package com.example.juan.buscachinos.presentation.map

import com.example.juan.buscachinos.domain.model.Chino
import com.example.juan.buscachinos.domain.model.GeoPoint

data class MapUiState(
    val chinos: List<Chino> = emptyList(),
    val selectedChinoId: Long? = null,
    val initialCameraTarget: GeoPoint = GeoPoint(latitude = 0.0, longitude = 0.0)
)
