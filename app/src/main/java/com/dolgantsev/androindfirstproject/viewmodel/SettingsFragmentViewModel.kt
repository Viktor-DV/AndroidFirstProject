package com.dolgantsev.androindfirstproject.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dolgantsev.androindfirstproject.App
import com.dolgantsev.androindfirstproject.data.dto.PreferenceProvider
import com.dolgantsev.androindfirstproject.domain.Interactor
import javax.inject.Inject

class SettingsFragmentViewModel : ViewModel(), SharedPreferences.OnSharedPreferenceChangeListener {
    @Inject
    lateinit var interactor: Interactor
    val categoryPropertyLiveData: MutableLiveData<String> = MutableLiveData()

    init {
        App.instance.dagger.inject(this)
        getCategoryProperty()
        App.instance.preferences.registerListener(this)
    }

    private fun getCategoryProperty() {
        categoryPropertyLiveData.value = interactor.getDefaultCategoryFromPreferences()
    }

    fun putCategoryProperty(category: String) {
        interactor.saveDefaultCategoryToPreferences(category)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == PreferenceProvider.CATEGORY_KEY) {
            getCategoryProperty()
        }
    }

    override fun onCleared() {
        super.onCleared()
        App.instance.preferences.unregisterListener(this)
    }
}
