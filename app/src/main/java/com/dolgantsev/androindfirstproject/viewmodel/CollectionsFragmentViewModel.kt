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

class CollectionsFragmentViewModel : ViewModel() {

    private val _collections = MutableStateFlow<Map<String, List<Film>>>(emptyMap())
    val collections: StateFlow<Map<String, List<Film>>> get() = _collections

    @Inject
    lateinit var interactor: Interactor

    private val _errorEvent = SingleLiveEvent<String>()
    val errorEvent: SingleLiveEvent<String> get() = _errorEvent

    init {
        App.instance.dagger.inject(this)
    }

    fun loadCollections() {
        viewModelScope.launch {
            val categories = listOf("popular", "top_rated", "upcoming", "now_playing")
            val collectionsMap = mutableMapOf<String, List<Film>>()
            categories.forEach { category ->
                val result = interactor.getFilmsFromApi(page = 1, category = category)
                result.onSuccess { films ->
                    collectionsMap[category] = films
                }.onFailure {
                    _errorEvent.postValue("Ошибка загрузки данных для категории: $category")
                }
            }
            _collections.value = collectionsMap
        }
    }
}