package com.dolgantsev.androindfirstproject.core.moduls

import android.content.Context
import com.dolgantsev.androindfirstproject.core.dto.PreferenceProvider
import com.dolgantsev.androindfirstproject.core.interactors.Interactor
import com.dolgantsev.androindfirstproject.core.repository.MainRepository
import com.dolgantsev.androindfirstproject.domain.DatabaseSource
import com.dolgantsev.androindfirstproject.network.api.TmdbApi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DomainModule {

    @Provides
    @Singleton
    fun providePreferenceProvider(context: Context): PreferenceProvider {
        return PreferenceProvider(context)
    }

    @Provides
    @Singleton
    fun provideMainRepository(databaseSource: DatabaseSource, tmdbApi: TmdbApi): MainRepository {
        return MainRepository(databaseSource, tmdbApi)
    }

    @Provides
    @Singleton
    fun provideInteractor(repository: MainRepository, preferenceProvider: PreferenceProvider): Interactor {
        return Interactor(repository, preferenceProvider)
    }
}