package com.example.juan.buscachinos.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.juan.buscachinos.domain.model.GeoPoint
import com.example.juan.buscachinos.domain.usecase.AddChinoUseCase
import com.example.juan.buscachinos.domain.usecase.DeleteChinoUseCase
import com.example.juan.buscachinos.domain.usecase.GetLastKnownLocationUseCase
import com.example.juan.buscachinos.domain.usecase.ObserveChinosUseCase
import com.example.juan.buscachinos.domain.usecase.SearchChinosUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MapViewModel(
    private val observeChinosUseCase: ObserveChinosUseCase,
    private val addChinoUseCase: AddChinoUseCase,
    private val deleteChinoUseCase: DeleteChinoUseCase,
    private val searchChinosUseCase: SearchChinosUseCase,
    getLastKnownLocationUseCase: GetLastKnownLocationUseCase
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")

    private val _uiState = MutableStateFlow(
        MapUiState(
            initialCameraTarget = getLastKnownLocationUseCase()
                ?: GeoPoint(latitude = 0.0, longitude = 0.0)
        )
    )
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(observeChinosUseCase(), searchQuery) { chinos, query ->
                chinos to searchChinosUseCase(chinos, query)
            }.collect { (allChinos, visibleChinos) ->
                _uiState.update { it.copy(chinos = visibleChinos, allChinos = allChinos) }
            }
        }
    }

    /** Taguea un chino en [location] con nombre [name] (long press en el mapa). */
    fun tagChino(location: GeoPoint, name: String) {
        viewModelScope.launch {
            addChinoUseCase(name, location)
        }
    }

    /** Busca por nombre (accion de buscar del SearchView). Vacio = mostrar todos. */
    fun search(query: String) {
        searchQuery.value = query
    }

    fun selectChino(id: Long) {
        _uiState.update { it.copy(selectedChinoId = id) }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedChinoId = null) }
    }

    fun deleteSelectedChino() {
        val id = _uiState.value.selectedChinoId ?: return
        viewModelScope.launch {
            deleteChinoUseCase(id)
            _uiState.update { it.copy(selectedChinoId = null) }
        }
    }
}
