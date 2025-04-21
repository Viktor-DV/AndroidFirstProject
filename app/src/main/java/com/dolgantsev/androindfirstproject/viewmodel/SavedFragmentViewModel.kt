package com.dolgantsev.androindfirstproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dolgantsev.androindfirstproject.App
import com.dolgantsev.androindfirstproject.domain.Film
import com.dolgantsev.androindfirstproject.domain.Interactor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SavedFragmentViewModel : ViewModel() {

    private val _savedFilms = MutableStateFlow<List<Film>>(emptyList())
    val savedFilms: StateFlow<List<Film>> get() = _savedFilms

    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
    }

    fun getSaved() {
        viewModelScope.launch {
            interactor.getFilmsFromDB().onSuccess { films ->
                val saved = films.filter { it.isSaved }
                _savedFilms.value = saved
            }.onFailure { e ->
                _savedFilms.value = emptyList()
            }
        }
    }
}