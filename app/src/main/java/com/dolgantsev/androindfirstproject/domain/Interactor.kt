package com.dolgantsev.androindfirstproject.domain

import com.dolgantsev.androindfirstproject.data.dto.MainRepository
import javax.inject.Inject

class Interactor @Inject constructor(
    private val repository: MainRepository
) {

    suspend fun getFilmsFromApi(page: Int, category: String?): Result<List<Film>> {
        return repository.getFilmsFromApi(page, category)
    }

    suspend fun getFilmsFromDB(): List<Film> {
        return repository.getAllFromDB()
    }

    suspend fun updateFilm(film: Film) {
        repository.updateFilm(film)
    }
}