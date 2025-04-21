package com.dolgantsev.androindfirstproject.data.dto

import com.dolgantsev.androindfirstproject.data.dao.FilmDao
import com.dolgantsev.androindfirstproject.domain.Film
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainRepository(private val filmDao: FilmDao) {

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