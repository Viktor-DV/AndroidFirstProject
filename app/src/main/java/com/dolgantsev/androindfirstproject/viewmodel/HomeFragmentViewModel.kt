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

    private val _filmsList = MutableStateFlow<List<Film>>(emptyList())
    val filmsList: StateFlow<List<Film>> get() = _filmsList

    private val _showProgressBar = MutableStateFlow(false)
    val showProgressBar: StateFlow<Boolean> get() = _showProgressBar

    @Inject
    lateinit var interactor: Interactor

    private val _errorEvent = SingleLiveEvent<String>()
    val errorEvent: SingleLiveEvent<String> get() = _errorEvent

    init {
        App.instance.dagger.inject(this)
    }

    fun getFilms() {
        viewModelScope.launch {
            _showProgressBar.value = true
            val result = interactor.getFilmsFromApi(1, null)
            _showProgressBar.value = false
            result.onSuccess { films ->
                _filmsList.value = films
            }.onFailure {
                _errorEvent.postValue("Ошибка загрузки данных с сервера")
            }
        }
    }

    fun searchFilms(query: String) {
        viewModelScope.launch {
            val currentFilms = _filmsList.value
            if (query.isBlank()) {
                getFilms() // Если запрос пустой, загружаем полный список
            } else {
                val filteredFilms = currentFilms.filter {
                    it.title.contains(query, ignoreCase = true)
                }
                _filmsList.value = filteredFilms
            }
        }
    }

    fun filterHighRatedFilms() {
        viewModelScope.launch {
            val highRatedFilms = _filmsList.value.filter { it.rating > 7.0 }
            _filmsList.value = highRatedFilms
        }
    }

    fun clearFilms() {
        viewModelScope.launch {
            _filmsList.value = emptyList()
        }
    }
}