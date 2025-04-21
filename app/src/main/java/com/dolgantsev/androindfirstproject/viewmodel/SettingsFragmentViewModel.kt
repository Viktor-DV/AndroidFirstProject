package com.dolgantsev.androindfirstproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dolgantsev.androindfirstproject.App
import com.dolgantsev.androindfirstproject.data.dto.PreferenceProvider
import com.dolgantsev.androindfirstproject.domain.Interactor
import com.dolgantsev.androindfirstproject.utils.asFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsFragmentViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor

    private val _categoryProperty = MutableStateFlow<String?>(null)
    val categoryProperty: StateFlow<String?> get() = _categoryProperty

    init {
        App.instance.dagger.inject(this)
        observeCategoryChanges()
    }

    private fun observeCategoryChanges() {
        viewModelScope.launch {
            App.instance.preferences.asFlow(PreferenceProvider.CATEGORY_KEY).collect { category ->
                _categoryProperty.value = category
            }
        }
    }

    fun putCategoryProperty(category: String) {
        interactor.saveDefaultCategoryToPreferences(category)
    }
}