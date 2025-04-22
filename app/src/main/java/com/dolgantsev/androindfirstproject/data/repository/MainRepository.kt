package com.dolgantsev.androindfirstproject.data.repository

import com.dolgantsev.androindfirstproject.api.APIKEY
import com.dolgantsev.androindfirstproject.api.TmdbApi
import com.dolgantsev.androindfirstproject.data.dao.FilmDao
import com.dolgantsev.androindfirstproject.data.dto.TmdbFilm
import com.dolgantsev.androindfirstproject.data.dto.TmdbResultsDto
import com.dolgantsev.androindfirstproject.domain.Film
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val filmDao: FilmDao,
    private val tmdbApi: TmdbApi
) {

    suspend fun getFilmsFromApi(page: Int, category: String?): Result<List<Film>> = withContext(Dispatchers.IO) {
        try {
            val response = tmdbApi.getFilms(
                category ?: "popular",
                APIKEY.KEY,
                "ru-RU",
                page
            )
            if (response.isSuccessful) {
                val body: TmdbResultsDto? = response.body()
                val films: List<Film> = if (body != null && body.results != null) {
                    body.results.map { tmdbFilm: TmdbFilm ->
                        tmdbFilm.toFilm()
                    }
                } else {
                    emptyList()
                }
                Result.success(films)
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun putToDb(film: Film) = withContext(Dispatchers.IO) {
        filmDao.insert(film)
    }

    suspend fun putToDb(films: List<Film>) = withContext(Dispatchers.IO) {
        filmDao.insertAll(films)
    }

    suspend fun getAllFromDB(): List<Film> = withContext(Dispatchers.IO) {
        filmDao.getCachedFilms()
    }

    suspend fun updateFilm(film: Film) = withContext(Dispatchers.IO) {
        filmDao.update(film)
    }

    suspend fun deleteFilm(title: String) = withContext(Dispatchers.IO) {
        filmDao.deleteByTitle(title)
    }

    suspend fun getFilmsByRating(minRating: Double): List<Film> = withContext(Dispatchers.IO) {
        filmDao.getFilmsByRating(minRating)
    }

    suspend fun getFilmByTitle(title: String): Film? = withContext(Dispatchers.IO) {
        filmDao.getFilmByTitle(title)
    }
}