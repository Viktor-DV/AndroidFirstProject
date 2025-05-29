package com.dolgantsev.androindfirstproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dolgantsev.androindfirstproject.App
import com.dolgantsev.androindfirstproject.core.interactors.Interactor
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class FavoritesFragmentViewModel : ViewModel() {

    private val _favoritesList = MutableLiveData<List<com.dolgantsev.androindfirstproject.domain.Film>>(emptyList())
    val favoritesList: LiveData<List<com.dolgantsev.androindfirstproject.domain.Film>> get() = _favoritesList

    @Inject
    lateinit var interactor: Interactor

    private val disposables = CompositeDisposable()

    init {
        App.instance.appComponent.inject(this)
    }

    fun getFavorites() {
        interactor.getFilmsFromDB()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { films -> films.filter { it.isInFavorites } }
            .subscribe(
                { favorites ->
                    _favoritesList.value = favorites
                },
                { error ->
                    _favoritesList.value = emptyList()
                }
            )
            .also { disposables.add(it) }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}