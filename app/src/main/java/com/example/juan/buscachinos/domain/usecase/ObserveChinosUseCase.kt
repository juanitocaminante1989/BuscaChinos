package com.example.juan.buscachinos.domain.usecase

import com.example.juan.buscachinos.domain.model.Chino
import com.example.juan.buscachinos.domain.repository.ChinoRepository
import kotlinx.coroutines.flow.Flow

class ObserveChinosUseCase(private val repository: ChinoRepository) {
    operator fun invoke(): Flow<List<Chino>> = repository.observeChinos()
}
