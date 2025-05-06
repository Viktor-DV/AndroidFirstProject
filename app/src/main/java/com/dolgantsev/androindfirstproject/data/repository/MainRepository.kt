package com.dolgantsev.androindfirstproject.data.repository

import com.dolgantsev.androindfirstproject.api.APIKEY
import com.dolgantsev.androindfirstproject.api.TmdbApi
import com.dolgantsev.androindfirstproject.data.dao.FilmDao
import com.dolgantsev.androindfirstproject.data.dto.TmdbFilm
import com.dolgantsev.androindfirstproject.domain.Film
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val filmDao: FilmDao,
    private val tmdbApi: TmdbApi
) {
    fun getFilmsFromApi(page: Int, category: String?): Single<List<Film>> {
        return tmdbApi.getPopularMovies(
            apiKey = APIKEY.KEY,
            language = "ru-RU",
            page = page
        )
            .subscribeOn(Schedulers.io())
            .map { response ->
                response.results.map { tmdbFilm ->
                    tmdbFilm.toFilm()
                }
            }
    }

    fun searchFilms(query: String, page: Int): Single<List<Film>> {
        return tmdbApi.searchMovies(
            apiKey = APIKEY.KEY,
            language = "ru-RU",
            query = query,
            page = page
        )
            .subscribeOn(Schedulers.io())
            .map { response ->
                response.results.map { tmdbFilm ->
                    tmdbFilm.toFilm()
                }
            }
    }

    fun putToDb(film: Film): Completable {
        return Completable.fromCallable { filmDao.insert(film) }
            .subscribeOn(Schedulers.io())
    }

    fun putToDb(films: List<Film>): Completable {
        return Completable.fromCallable { filmDao.insertAll(films) }
            .subscribeOn(Schedulers.io())
    }

    fun getAllFromDB(): Single<List<Film>> {
        return Single.fromCallable { filmDao.getCachedFilms() }
            .subscribeOn(Schedulers.io())
    }

    fun updateFilm(film: Film): Completable {
        return Completable.fromCallable { filmDao.update(film) }
            .subscribeOn(Schedulers.io())
    }

    fun deleteFilm(title: String): Completable {
        return Completable.fromCallable { filmDao.deleteByTitle(title) }
            .subscribeOn(Schedulers.io())
    }

    fun getFilmsByRating(minRating: Double): Single<List<Film>> {
        return Single.fromCallable { filmDao.getFilmsByRating(minRating) }
            .subscribeOn(Schedulers.io())
    }

    fun getFilmByTitle(title: String): Maybe<Film> {
        return Maybe.fromCallable { filmDao.getFilmByTitle(title) }
            .subscribeOn(Schedulers.io())
    }

    private fun TmdbFilm.toFilm(): Film {
        return Film(
            id = id,
            title = title,
            overview = overview,
            posterPath = posterPath,
            rating = voteAverage,
            isInFavorites = false,
            isSaved = false
        )
    }
}