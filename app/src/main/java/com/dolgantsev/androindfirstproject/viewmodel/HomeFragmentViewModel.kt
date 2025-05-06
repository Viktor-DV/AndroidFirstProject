package com.dolgantsev.androindfirstproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dolgantsev.androindfirstproject.App
import com.dolgantsev.androindfirstproject.domain.Film
import com.dolgantsev.androindfirstproject.domain.Interactor
import com.dolgantsev.androindfirstproject.utils.SingleLiveEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit
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
    private val searchSubject = PublishSubject.create<String>()
    private var currentPage = 1
    private var currentQuery = ""

    init {
        App.instance.dagger.inject(this)
        setupSearchObservable()
    }

    private fun setupSearchObservable() {
        disposables.add(
            searchSubject
                .debounce(300, TimeUnit.MILLISECONDS) // Задержка 300 мс
                .distinctUntilChanged() // Игнорируем повторяющиеся запросы
                .switchMap { query ->
                    currentQuery = query
                    currentPage = 1 // Сбрасываем страницу для нового запроса
                    performSearch(query, currentPage)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { films ->
                        _filmsList.value = films
                        _showProgressBar.value = false
                    },
                    { error ->
                        _errorEvent.postValue("Ошибка поиска: ${error.message}")
                        _showProgressBar.value = false
                    }
                )
        )
    }

    private fun performSearch(query: String, page: Int): Observable<List<Film>> {
        if (query.length < 3) { // Поиск только при 3+ символах
            return Observable.just(emptyList())
        }
        _showProgressBar.value = true
        return interactor.searchFilms(query, page)
            .toObservable()
            .onErrorReturn { emptyList() }
            .doOnTerminate { _showProgressBar.value = false }
    }

    fun searchFilms(query: String) {
        searchSubject.onNext(query)
    }

    fun loadNextPage() {
        if (currentQuery.isEmpty()) return
        _showProgressBar.value = true
        disposables.add(
            interactor.searchFilms(currentQuery, ++currentPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { newFilms ->
                        val currentList = _filmsList.value.orEmpty().toMutableList()
                        currentList.addAll(newFilms)
                        _filmsList.value = currentList
                        _showProgressBar.value = false
                    },
                    { error ->
                        _errorEvent.postValue("Ошибка загрузки следующей страницы: ${error.message}")
                        _showProgressBar.value = false
                    }
                )
        )
    }

    fun getFilms() {
        _showProgressBar.value = true
        disposables.add(
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
        )
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