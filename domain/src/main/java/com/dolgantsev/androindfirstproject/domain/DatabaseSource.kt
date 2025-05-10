package com.dolgantsev.androindfirstproject.domain

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

interface DatabaseSource {
    fun putToDb(film: Film): Completable
    fun putToDb(films: List<Film>): Completable
    fun getAllFromDB(): Single<List<Film>>
    fun updateFilm(film: Film): Completable
    fun deleteFilm(id: Int): Completable
    fun getFilmsByRating(minRating: Double): Single<List<Film>>
    fun getFilmById(id: Int): Maybe<Film>
}