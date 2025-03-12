package com.dolgantsev.androindfirstproject.di

import com.dolgantsev.androindfirstproject.di.moduls.DatabaseModule
import com.dolgantsev.androindfirstproject.di.moduls.DomainModule
import com.dolgantsev.androindfirstproject.di.moduls.RemoteModule
import com.dolgantsev.androindfirstproject.viewmodel.HomeFragmentViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    //Внедряем все модули, нужные для этого компонента
    modules = [
        RemoteModule::class,
        DatabaseModule::class,
        DomainModule::class
    ]
)
interface AppComponent {
    //метод для того, чтобы появилась внедрять зависимости в HomeFragmentViewModel
    fun inject(homeFragmentViewModel: HomeFragmentViewModel)
}