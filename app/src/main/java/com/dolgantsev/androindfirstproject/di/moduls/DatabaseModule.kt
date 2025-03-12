package com.dolgantsev.androindfirstproject.di.moduls

import com.dolgantsev.androindfirstproject.data.dto.MainRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface DatabaseModule {
    @Binds
    @Singleton
    fun bindRepository(impl: MainRepository): MainRepository
}