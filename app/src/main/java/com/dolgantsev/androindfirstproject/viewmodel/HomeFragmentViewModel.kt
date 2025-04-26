package com.dolgantsev.androindfirstproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dolgantsev.androindfirstproject.App
import com.dolgantsev.androindfirstproject.domain.Film
import com.dolgantsev.androindfirstproject.domain.Interactor
import com.dolgantsev.androindfirstproject.utils.SingleLiveEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class HomeFragmentViewModel : ViewModel() {

    private val _filmsList = MutableLiveData<List<Film>>(emptyList())
    val filmsList: LiveData<List<Film>> get() = _filmsList

    private val _showProgressBar = MutableLiveData(false)
    val showProgressBar: LiveData<Boolean> get() = _showProgressBar

    @Inject
    lateinit var interactor: Interactor

    private val _errorEvent = SingleLiveEvent<String>()
    val errorEvent: SingleLiveEvent<String> get() = _errorEvent

    private val disposables = CompositeDisposable()

    init {
        App.instance.dagger.inject(this)
    }

    fun getFilms() {
        _showProgressBar.value = true
        interactor.getFilmsFromApi(1, null)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { films ->
                    _filmsList.value = films
                    _showProgressBar.value = false
                },
                { error ->
                    _errorEvent.postValue("Ошибка загрузки данных с сервера")
                    _showProgressBar.value = false
                }
            )
            .also { disposables.add(it) }
    }

    fun searchFilms(query: String) {
        val currentFilms = _filmsList.value ?: emptyList()
        if (query.isBlank()) {
            getFilms()
        } else {
            val filteredFilms = currentFilms.filter {
                it.title.contains(query, ignoreCase = true)
            }
            _filmsList.value = filteredFilms
        }
    }

    fun filterHighRatedFilms() {
        val highRatedFilms = (_filmsList.value ?: emptyList()).filter { it.rating > 7.0 }
        _filmsList.value = highRatedFilms
    }

    fun clearFilms() {
        _filmsList.value = emptyList()
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}