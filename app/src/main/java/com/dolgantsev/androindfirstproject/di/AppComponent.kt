package com.dolgantsev.androindfirstproject.di

import android.content.Context
import com.dolgantsev.androindfirstproject.core.dto.PreferenceProvider
import com.dolgantsev.androindfirstproject.core.interactors.Interactor
import com.dolgantsev.androindfirstproject.core.moduls.DomainModule
import com.dolgantsev.androindfirstproject.core.repository.MainRepository
import com.dolgantsev.androindfirstproject.database.dao.FilmDao
import com.dolgantsev.androindfirstproject.database.db.AppDatabase
import com.dolgantsev.androindfirstproject.database.di.DatabaseModule
import com.dolgantsev.androindfirstproject.domain.DatabaseSource
import com.dolgantsev.androindfirstproject.network.api.TmdbApi
import com.dolgantsev.androindfirstproject.network.di.NetworkModule
import com.dolgantsev.androindfirstproject.viewmodel.*
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NetworkModule::class,
        DatabaseModule::class,
        DomainModule::class
    ]
)
interface AppComponent {

    fun inject(homeFragmentViewModel: HomeFragmentViewModel)
    fun inject(settingsFragmentViewModel: SettingsFragmentViewModel)
    fun inject(collectionsFragmentViewModel: CollectionsFragmentViewModel)
    fun inject(favoritesFragmentViewModel: FavoritesFragmentViewModel)
    fun inject(savedFragmentViewModel: SavedFragmentViewModel)
    fun inject(detailsFragmentViewModel: DetailsFragmentViewModel)

    fun inject(mainRepository: MainRepository)
    fun inject(interactor: Interactor)

    fun getTmdbApi(): TmdbApi
    fun filmDao(): FilmDao
    fun appDatabase(): AppDatabase
    fun databaseSource(): DatabaseSource
    fun preferenceProvider(): PreferenceProvider

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}
