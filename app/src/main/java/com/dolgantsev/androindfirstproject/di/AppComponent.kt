package com.dolgantsev.androindfirstproject.di

import com.dolgantsev.androindfirstproject.di.moduls.DatabaseModule
import com.dolgantsev.androindfirstproject.di.moduls.DomainModule
import com.dolgantsev.androindfirstproject.di.moduls.RemoteModule
import com.dolgantsev.androindfirstproject.viewmodel.HomeFragmentViewModel
import com.dolgantsev.androindfirstproject.viewmodel.SettingsFragmentViewModel
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
    //метод для того, чтобы появилась возможность внедрять зависимости в HomeFragmentViewModel
    fun inject(homeFragmentViewModel: HomeFragmentViewModel)
    //метод для того, чтобы появилась возможность внедрять зависимости в SettingsFragmentViewModel
    fun inject(settingsFragmentViewModel: SettingsFragmentViewModel)
}