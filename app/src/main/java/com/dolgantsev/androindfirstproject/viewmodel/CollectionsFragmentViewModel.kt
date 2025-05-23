package com.dolgantsev.androindfirstproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dolgantsev.androindfirstproject.App
import com.dolgantsev.androindfirstproject.core.interactors.Interactor
import com.dolgantsev.androindfirstproject.utils.SingleLiveEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class CollectionsFragmentViewModel : ViewModel() {

    private val _collections = MutableLiveData<Map<String, List<com.dolgantsev.androindfirstproject.domain.Film>>>(emptyMap())
    val collections: LiveData<Map<String, List<com.dolgantsev.androindfirstproject.domain.Film>>> get() = _collections

    @Inject
    lateinit var interactor: Interactor

    private val _errorEvent = SingleLiveEvent<String>()
    val errorEvent: SingleLiveEvent<String> get() = _errorEvent

    private val disposables = CompositeDisposable()

    init {
        App.instance.appComponent.inject(this)
    }

    fun loadCollections() {
        val categories = listOf("popular", "top_rated", "upcoming", "now_playing")
        val collectionsMap = mutableMapOf<String, List<com.dolgantsev.androindfirstproject.domain.Film>>()

        categories.forEach { category ->
            interactor.getFilmsFromApi(page = 1, category = category)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { films ->
                        collectionsMap[category] = films
                        if (collectionsMap.size == categories.size) {
                            _collections.value = collectionsMap
                        }
                    },
                    { error ->
                        _errorEvent.postValue("Ошибка загрузки данных для категории: $category")
                    }
                )
                .also { disposables.add(it) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}