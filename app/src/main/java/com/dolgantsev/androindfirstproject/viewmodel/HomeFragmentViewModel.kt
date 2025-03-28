package com.dolgantsev.androindfirstproject.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dolgantsev.androindfirstproject.App
import com.dolgantsev.androindfirstproject.domain.Film
import com.dolgantsev.androindfirstproject.domain.Interactor
import javax.inject.Inject

class HomeFragmentViewModel : ViewModel() {
    val filmsListLiveData: MutableLiveData<List<Film>> = MutableLiveData()

    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
        getFilms()
    }

    fun getFilms() {
        interactor.getFilmsFromApi(1, object : ApiCallback {
            override fun onSuccess(films: List<Film>) {
                filmsListLiveData.postValue(films)
            }

            override fun onFailure() {
                filmsListLiveData.postValue(interactor.getFilmsFromDB())
            }
        })
    }

    // Пример: обновление фильма
    fun updateFilm(film: Film) {
        interactor.updateFilm(film)
        getFilms() // Перезагрузка списка после обновления
    }

    // Пример: удаление фильма
    fun deleteFilm(title: String) {
        interactor.deleteFilm(title)
        getFilms() // Перезагрузка списка после удаления
    }

    // Пример: получение фильмов с рейтингом выше 7.0
    fun getHighRatedFilms() {
        filmsListLiveData.postValue(interactor.getFilmsByRating(7.0))
    }

    // Пример: получение фильма по названию
    fun getFilmByTitle(title: String) {
        val film = interactor.getFilmByTitle(title)
        film?.let { filmsListLiveData.postValue(listOf(it)) }
    }

    interface ApiCallback {
        fun onSuccess(films: List<Film>)
        fun onFailure()
    }
}