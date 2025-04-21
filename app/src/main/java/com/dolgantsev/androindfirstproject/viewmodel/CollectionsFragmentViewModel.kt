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

class CollectionsFragmentViewModel : ViewModel() {

    private val _collections = MutableStateFlow<Map<String, List<Film>>>(emptyMap())
    val collections: StateFlow<Map<String, List<Film>>> get() = _collections

    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
        loadCollections()
    }

    fun loadCollections() {
        viewModelScope.launch {
            val categories = listOf("popular", "top_rated", "upcoming", "now_playing")
            val collectionsMap = mutableMapOf<String, List<Film>>()
            categories.forEach { category ->
                val films = interactor.getFilmsFromApi(page = 1, category = category).getOrDefault(emptyList())
                collectionsMap[category] = films
            }
            _collections.value = collectionsMap
        }
    }
}