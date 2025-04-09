package com.dolgantsev.androindfirstproject.data.dto

import androidx.lifecycle.LiveData
import com.dolgantsev.androindfirstproject.data.dao.FilmDao
import com.dolgantsev.androindfirstproject.domain.Film
import java.util.concurrent.Executors

class MainRepository(private val filmDao: FilmDao) {

    fun putToDb(film: Film) {
        Executors.newSingleThreadExecutor().execute {
            filmDao.insert(film)
        }
    }

    fun putToDb(films: List<Film>) {
        Executors.newSingleThreadExecutor().execute {
            filmDao.insertAll(films)
        }
    }

    fun getAllFromDB(): LiveData<List<Film>> = filmDao.getCachedFilms()

    fun updateFilm(film: Film) {
        Executors.newSingleThreadExecutor().execute {
            filmDao.update(film)
        }
    }

    fun deleteFilm(title: String) {
        Executors.newSingleThreadExecutor().execute {
            filmDao.deleteByTitle(title)
        }
    }

    fun getFilmsByRating(minRating: Double): List<Film> {
        return filmDao.getFilmsByRating(minRating)
    }

    fun getFilmByTitle(title: String): Film? {
        return filmDao.getFilmByTitle(title)
    }
}
