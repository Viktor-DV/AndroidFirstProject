package com.dolgantsev.androindfirstproject.core.interactors

import com.dolgantsev.androindfirstproject.core.dto.PreferenceProvider
import com.dolgantsev.androindfirstproject.core.repository.MainRepository
import com.dolgantsev.androindfirstproject.domain.Film
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class Interactor @Inject constructor(
    private val repository: MainRepository,
    private val preferenceProvider: PreferenceProvider
) {

    fun getFilmsFromApi(page: Int, category: String?): Single<List<Film>> {
        return repository.getFilmsFromApi(page, category)
    }

    fun searchFilms(query: String, page: Int): Single<List<Film>> {
        return repository.searchFilms(query, page)
    }

    fun getFilmsFromDB(): Single<List<Film>> {
        return repository.getAllFromDB()
    }

    fun updateFilm(film: Film): Completable {
        return repository.updateFilm(film)
    }

    fun saveDefaultCategoryToPreferences(category: String) {
        preferenceProvider.saveCategory(category)
    }

    fun getFilmById(id: Int): Maybe<Film> {
        return repository.getFilmById(id)
    }

    fun clearAllFilms(): Completable {
        return repository.clearAllFilms()
    }
}