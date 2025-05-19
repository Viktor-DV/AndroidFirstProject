package com.dolgantsev.androindfirstproject.database

import com.dolgantsev.androindfirstproject.database.dao.FilmDao
import com.dolgantsev.androindfirstproject.domain.DatabaseSource
import com.dolgantsev.androindfirstproject.domain.Film
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class DatabaseSourceImpl @Inject constructor(
    private val filmDao: FilmDao
) : DatabaseSource {

    override fun putToDb(film: Film): Completable {
        return filmDao.insert(film)
            .subscribeOn(Schedulers.io())
    }

    override fun putToDb(films: List<Film>): Completable {
        return filmDao.insertAll(films)
            .subscribeOn(Schedulers.io())
    }

    override fun getAllFromDB(): Single<List<Film>> {
        return filmDao.getCachedFilms()
            .subscribeOn(Schedulers.io())
    }

    override fun updateFilm(film: Film): Completable {
        return filmDao.update(film)
            .subscribeOn(Schedulers.io())
    }

    override fun deleteFilm(id: Int): Completable {
        return filmDao.deleteById(id)
            .subscribeOn(Schedulers.io())
    }

    override fun getFilmsByRating(minRating: Double): Single<List<Film>> {
        return filmDao.getFilmsByRating(minRating)
            .subscribeOn(Schedulers.io())
    }

    override fun getFilmById(id: Int): Maybe<Film> {
        return filmDao.getFilmById(id)
            .subscribeOn(Schedulers.io())
    }

    override fun clearAllFilms(): Completable {
        return filmDao.clearAllFilms()
            .subscribeOn(Schedulers.io())
    }
}