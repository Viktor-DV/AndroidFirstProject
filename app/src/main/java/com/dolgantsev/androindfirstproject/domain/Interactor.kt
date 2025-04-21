package com.dolgantsev.androindfirstproject.domain

import com.dolgantsev.androindfirstproject.api.APIKEY
import com.dolgantsev.androindfirstproject.api.TmdbApi
import com.dolgantsev.androindfirstproject.data.dto.MainRepository
import com.dolgantsev.androindfirstproject.data.dto.PreferenceProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Interactor(
    private val repo: MainRepository,
    private val retrofitService: TmdbApi,
    private val preferences: PreferenceProvider
) {
    suspend fun getFilmsFromApi(page: Int, category: String? = null): Result<List<Film>> = withContext(Dispatchers.IO) {
        try {
            val effectiveCategory = category ?: getDefaultCategoryFromPreferences()
            val response = retrofitService.getFilms(
                effectiveCategory,
                APIKEY.KEY,
                "ru-RU",
                page
            )
            if (response.isSuccessful) {
                val tmdbResults = response.body()
                val films = tmdbResults?.tmdbFilms?.map { tmdbFilm ->
                    Film(
                        id = tmdbFilm.id,
                        title = tmdbFilm.title,
                        poster = tmdbFilm.posterPath,
                        description = tmdbFilm.overview,
                        rating = tmdbFilm.voteAverage,
                        isInFavorites = false,
                        isSaved = false
                    )
                } ?: emptyList()
                films.forEach { repo.putToDb(it) }
                Result.success(films)
            } else {
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFilmsFromDB(): List<Film> = withContext(Dispatchers.IO) {
        repo.getAllFromDB()
    }

    suspend fun updateFilm(film: Film) = withContext(Dispatchers.IO) {
        repo.updateFilm(film)
    }

    suspend fun deleteFilm(title: String) = withContext(Dispatchers.IO) {
        repo.deleteFilm(title)
    }

    suspend fun getFilmsByRating(minRating: Double): List<Film> = withContext(Dispatchers.IO) {
        repo.getFilmsByRating(minRating)
    }

    suspend fun getFilmByTitle(title: String): Film? = withContext(Dispatchers.IO) {
        repo.getFilmByTitle(title)
    }

    fun saveDefaultCategoryToPreferences(category: String) {
        preferences.saveDefaultCategory(category)
    }

    fun getDefaultCategoryFromPreferences(): String {
        return preferences.getDefaultCategory()
    }
}