package com.dolgantsev.androindfirstproject.di

import com.dolgantsev.androindfirstproject.di.moduls.DomainModule
import com.dolgantsev.androindfirstproject.di.moduls.RemoteModule
import com.dolgantsev.androindfirstproject.viewmodel.CollectionsFragmentViewModel
import com.dolgantsev.androindfirstproject.viewmodel.DetailsFragmentViewModel
import com.dolgantsev.androindfirstproject.viewmodel.FavoritesFragmentViewModel
import com.dolgantsev.androindfirstproject.viewmodel.HomeFragmentViewModel
import com.dolgantsev.androindfirstproject.viewmodel.SavedFragmentViewModel
import com.dolgantsev.androindfirstproject.viewmodel.SettingsFragmentViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [RemoteModule::class, DomainModule::class])
interface AppComponent {
    fun inject(homeFragmentViewModel: HomeFragmentViewModel)
    fun inject(settingsFragmentViewModel: SettingsFragmentViewModel)
    fun inject(collectionsFragmentViewModel: CollectionsFragmentViewModel)
    fun inject(favoritesFragmentViewModel: FavoritesFragmentViewModel) // Добавили
    fun inject(savedFragmentViewModel: SavedFragmentViewModel) // Добавили
    fun inject(detailsFragmentViewModel: DetailsFragmentViewModel) // Добавили
}