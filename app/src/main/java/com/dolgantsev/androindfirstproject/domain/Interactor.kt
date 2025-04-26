package com.dolgantsev.androindfirstproject.domain

import com.dolgantsev.androindfirstproject.data.dto.PreferenceProvider
import com.dolgantsev.androindfirstproject.data.repository.MainRepository
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

    fun getFilmsFromDB(): Single<List<Film>> {
        return repository.getAllFromDB()
    }

    fun updateFilm(film: Film): Completable {
        return repository.updateFilm(film)
    }

    fun saveDefaultCategoryToPreferences(category: String) {
        preferenceProvider.saveCategory(category)
    }

    fun getFilmByTitle(title: String): Maybe<Film> {
        return repository.getFilmByTitle(title)
    }
}