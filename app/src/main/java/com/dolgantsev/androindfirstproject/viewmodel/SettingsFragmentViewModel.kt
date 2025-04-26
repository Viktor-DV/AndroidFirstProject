package com.dolgantsev.androindfirstproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dolgantsev.androindfirstproject.App
import com.dolgantsev.androindfirstproject.data.dto.PreferenceProvider
import com.dolgantsev.androindfirstproject.domain.Interactor
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class SettingsFragmentViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor

    @Inject
    lateinit var preferenceProvider: PreferenceProvider

    private val _categoryProperty = MutableLiveData<String?>()
    val categoryProperty: LiveData<String?> get() = _categoryProperty

    private val disposables = CompositeDisposable()

    init {
        App.instance.dagger.inject(this)
        // Подписываемся на изменения в SharedPreferences через RxJava
        preferenceProvider.asObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { category ->
                    _categoryProperty.value = category ?: preferenceProvider.getCategory()
                },
                { error ->
                    // Обработка ошибки (например, логирование)
                }
            )
            .also { disposables.add(it) }

        // Устанавливаем начальное значение
        _categoryProperty.value = preferenceProvider.getCategory()
    }

    fun putCategoryProperty(category: String) {
        interactor.saveDefaultCategoryToPreferences(category)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}