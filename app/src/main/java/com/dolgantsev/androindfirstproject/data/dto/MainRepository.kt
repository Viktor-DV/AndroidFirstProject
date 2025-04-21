package com.dolgantsev.androindfirstproject.data.dto

import com.dolgantsev.androindfirstproject.api.APIKEY
import com.dolgantsev.androindfirstproject.api.TmdbApi
import com.dolgantsev.androindfirstproject.data.dao.FilmDao
import com.dolgantsev.androindfirstproject.domain.Film
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val filmDao: FilmDao,
    private val tmdbApi: TmdbApi
) {

    suspend fun getFilmsFromApi(page: Int, category: String?): List<Film> = withContext(Dispatchers.IO) {
        val response = tmdbApi.getFilms(
            category ?: "popular",
            APIKEY.KEY,
            "ru-RU",
            page
        )
        response.body()?.films?.map { it.toFilm() } ?: emptyList() // Исправили results на films
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