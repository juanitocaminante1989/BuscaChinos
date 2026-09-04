package com.example.juan.buscachinos.domain.usecase

import com.example.juan.buscachinos.domain.model.Chino

/** Filtra por nombre (contiene, sin distinguir mayusculas). Texto vacio = sin filtrar. */
class SearchChinosUseCase {
    operator fun invoke(chinos: List<Chino>, query: String): List<Chino> {
        if (query.isBlank()) return chinos
        return chinos.filter { it.name.contains(query, ignoreCase = true) }
    }
}
