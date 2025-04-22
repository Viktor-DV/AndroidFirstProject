package com.dolgantsev.androindfirstproject.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.dolgantsev.androindfirstproject.App
import com.dolgantsev.androindfirstproject.data.dto.PreferenceProvider
import com.dolgantsev.androindfirstproject.domain.Interactor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class SettingsFragmentViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor

    @Inject
    lateinit var preferenceProvider: PreferenceProvider

    private val _categoryProperty = MutableStateFlow<String?>(null)
    val categoryProperty: StateFlow<String?> get() = _categoryProperty

    private val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == PreferenceProvider.CATEGORY_KEY) {
            _categoryProperty.value = preferenceProvider.getCategory()
        }
    }

    init {
        App.instance.dagger.inject(this)
        // Инициализируем начальное значение категории
        _categoryProperty.value = preferenceProvider.getCategory()
        // Регистрируем слушатель
        preferenceProvider.registerListener(preferenceListener)
    }

    fun putCategoryProperty(category: String) {
        interactor.saveDefaultCategoryToPreferences(category)
    }

    override fun onCleared() {
        super.onCleared()
        // Снимаем слушатель при уничтожении ViewModel
        preferenceProvider.unregisterListener(preferenceListener)
    }
}