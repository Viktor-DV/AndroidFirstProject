package com.dolgantsev.androindfirstproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dolgantsev.androindfirstproject.App
import com.dolgantsev.androindfirstproject.domain.Film
import com.dolgantsev.androindfirstproject.domain.Interactor
import com.dolgantsev.androindfirstproject.utils.SingleLiveEvent
import javax.inject.Inject

class HomeFragmentViewModel : ViewModel() {

    val showProgressBar: MutableLiveData<Boolean> = MutableLiveData()

    val filmsListLiveData: LiveData<List<Film>>

    val errorEvent = SingleLiveEvent<String>()

    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
        filmsListLiveData = interactor.getFilmsFromDB() // Инициализация LiveData из БД
        getFilms()
    }

    fun getFilms() {
        showProgressBar.postValue(true)
        interactor.getFilmsFromApi(1, object : ApiCallback {
            override fun onSuccess() {
                showProgressBar.postValue(false)
            }

            override fun onFailure() {
                showProgressBar.postValue(false)
                errorEvent.call("Ошибка загрузки данных с сервера") // Отправляем событие ошибки
            }
        })
    }

    // обновление фильма
    fun updateFilm(film: Film) {
        interactor.updateFilm(film)
        getFilms()
    }

    // удаление фильма
    fun deleteFilm(title: String) {
        interactor.deleteFilm(title)
        getFilms()
    }

    // получение фильмов с рейтингом выше 7.0
    fun getHighRatedFilms() {
        interactor.getFilmsByRating(7.0)
    }

    // получение фильма по названию
    fun getFilmByTitle(title: String) {
        interactor.getFilmByTitle(title)
    }

    interface ApiCallback {
        fun onSuccess()
        fun onFailure()
    }
}