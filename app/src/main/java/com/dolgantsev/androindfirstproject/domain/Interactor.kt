package com.dolgantsev.androindfirstproject.domain

import com.dolgantsev.androindfirstproject.data.dto.PreferenceProvider
import com.dolgantsev.androindfirstproject.data.repository.MainRepository
import javax.inject.Inject

class Interactor @Inject constructor(
    private val repository: MainRepository,
    private val preferenceProvider: PreferenceProvider
) {

    suspend fun getFilmsFromApi(page: Int, category: String?): Result<List<Film>> {
        return repository.getFilmsFromApi(page, category)
    }

    suspend fun getFilmsFromDB(): Result<List<Film>> {
        return try {
            val films = repository.getAllFromDB()
            Result.success(films)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateFilm(film: Film) {
        repository.updateFilm(film)
    }

    fun saveDefaultCategoryToPreferences(category: String) {
        preferenceProvider.saveCategory(category)
    }
}