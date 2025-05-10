package com.dolgantsev.androindfirstproject.core.repository

import com.dolgantsev.androindfirstproject.domain.DatabaseSource
import com.dolgantsev.androindfirstproject.network.api.TmdbApi
import com.dolgantsev.androindfirstproject.network.api.APIKEY
import com.dolgantsev.androindfirstproject.network.dto.TmdbFilm
import com.dolgantsev.androindfirstproject.domain.Film
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val databaseSource: DatabaseSource,
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
        return databaseSource.putToDb(film)
    }

    fun putToDb(films: List<Film>): Completable {
        return databaseSource.putToDb(films)
    }

    fun getAllFromDB(): Single<List<Film>> {
        return databaseSource.getAllFromDB()
    }

    fun updateFilm(film: Film): Completable {
        return databaseSource.updateFilm(film)
    }

    fun deleteFilm(id: Int): Completable {
        return databaseSource.deleteFilm(id)
    }

    fun getFilmsByRating(minRating: Double): Single<List<Film>> {
        return databaseSource.getFilmsByRating(minRating)
    }

    fun getFilmById(id: Int): Maybe<Film> {
        return databaseSource.getFilmById(id)
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