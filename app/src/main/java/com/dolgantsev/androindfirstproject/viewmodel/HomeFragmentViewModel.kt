package com.dolgantsev.androindfirstproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dolgantsev.androindfirstproject.App
import com.dolgantsev.androindfirstproject.domain.Film
import com.dolgantsev.androindfirstproject.domain.Interactor
import com.dolgantsev.androindfirstproject.utils.SingleLiveEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeFragmentViewModel : ViewModel() {

    private val _showProgressBar = MutableStateFlow(false)
    val showProgressBar: StateFlow<Boolean> get() = _showProgressBar

    private val _filmsList = MutableStateFlow<List<Film>>(emptyList())
    val filmsList: StateFlow<List<Film>> get() = _filmsList

    val errorEvent = SingleLiveEvent<String>()

    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
        loadFilmsFromDb()
        getFilms()
    }

    fun getFilms() {
        viewModelScope.launch {
            _showProgressBar.value = true
            val result = interactor.getFilmsFromApi(1, null) // Явно передаём null для использования категории из настроек
            _showProgressBar.value = false
            result.onSuccess { films ->
                _filmsList.value = films
            }.onFailure {
                errorEvent.postCall("Ошибка загрузки данных с сервера")
            }
        }
    }

    private fun loadFilmsFromDb() {
        viewModelScope.launch {
            _filmsList.value = interactor.getFilmsFromDB()
        }
    }

    fun updateFilm(film: Film) {
        viewModelScope.launch {
            interactor.updateFilm(film)
            loadFilmsFromDb()
        }
    }

    fun deleteFilm(title: String) {
        viewModelScope.launch {
            interactor.deleteFilm(title)
            loadFilmsFromDb()
        }
    }

    fun getHighRatedFilms() {
        viewModelScope.launch {
            val films = interactor.getFilmsByRating(7.0)
            _filmsList.value = films
        }
    }

    fun getFilmByTitle(title: String) {
        viewModelScope.launch {
            val film = interactor.getFilmByTitle(title)
            if (film != null) {
                _filmsList.value = listOf(film)
            }
        }
    }
}