package com.example.juan.buscachinos.domain.usecase

import com.example.juan.buscachinos.domain.repository.ChinoRepository

class DeleteChinoUseCase(private val repository: ChinoRepository) {
    suspend operator fun invoke(id: Long) {
        repository.deleteChino(id)
    }
}
