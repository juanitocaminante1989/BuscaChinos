package com.example.juan.buscachinos.presentation.map

import com.example.juan.buscachinos.domain.model.Chino
import com.example.juan.buscachinos.domain.model.GeoPoint

data class MapUiState(
    /** Chinos visibles en el mapa (con el filtro de busqueda aplicado). */
    val chinos: List<Chino> = emptyList(),
    /** Todos los chinos tagueados, sin el filtro de busqueda (listado del drawer). */
    val allChinos: List<Chino> = emptyList(),
    val selectedChinoId: Long? = null,
    val initialCameraTarget: GeoPoint = GeoPoint(latitude = 0.0, longitude = 0.0)
)
