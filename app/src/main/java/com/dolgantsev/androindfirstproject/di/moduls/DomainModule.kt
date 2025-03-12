package com.dolgantsev.androindfirstproject.di.moduls

import com.dolgantsev.androindfirstproject.domain.Interactor
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface DomainModule {
    @Binds
    @Singleton
    fun bindInteractor(impl: Interactor): Interactor
}
