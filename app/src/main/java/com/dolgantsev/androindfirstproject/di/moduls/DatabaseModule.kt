package com.dolgantsev.androindfirstproject.di.moduls

import com.dolgantsev.androindfirstproject.data.dto.MainRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideRepository(): MainRepository {
        return MainRepository()
    }
}