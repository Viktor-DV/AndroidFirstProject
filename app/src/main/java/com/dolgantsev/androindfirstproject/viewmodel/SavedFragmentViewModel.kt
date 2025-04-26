package com.dolgantsev.androindfirstproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dolgantsev.androindfirstproject.App
import com.dolgantsev.androindfirstproject.domain.Film
import com.dolgantsev.androindfirstproject.domain.Interactor
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class SavedFragmentViewModel : ViewModel() {

    private val _savedFilms = MutableLiveData<List<Film>>(emptyList())
    val savedFilms: LiveData<List<Film>> get() = _savedFilms

    @Inject
    lateinit var interactor: Interactor

    private val disposables = CompositeDisposable()

    init {
        App.instance.dagger.inject(this)
    }

    fun getSaved() {
        interactor.getFilmsFromDB()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { films -> films.filter { it.isSaved } }
            .subscribe(
                { saved ->
                    _savedFilms.value = saved
                },
                { error ->
                    _savedFilms.value = emptyList()
                }
            )
            .also { disposables.add(it) }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}