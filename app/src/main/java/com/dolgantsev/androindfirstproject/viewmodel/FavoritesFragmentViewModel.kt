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

class FavoritesFragmentViewModel : ViewModel() {

    private val _favoritesList = MutableStateFlow<List<Film>>(emptyList())
    val favoritesList: StateFlow<List<Film>> get() = _favoritesList

    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            val allFilms = interactor.getFilmsFromDB()
            _favoritesList.value = allFilms.filter { it.isInFavorites }
        }
    }
}