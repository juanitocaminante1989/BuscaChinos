package com.example.juan.buscachinos.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.juan.buscachinos.AppContainer

class MapViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(MapViewModel::class.java)) {
            "Unknown ViewModel class: $modelClass"
        }
        return MapViewModel(
            observeChinosUseCase = container.observeChinosUseCase,
            addChinoUseCase = container.addChinoUseCase,
            deleteChinoUseCase = container.deleteChinoUseCase,
            getLastKnownLocationUseCase = container.getLastKnownLocationUseCase
        ) as T
    }
}
