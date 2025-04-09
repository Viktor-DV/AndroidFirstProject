package com.dolgantsev.androindfirstproject

import android.app.Application
import com.dolgantsev.androindfirstproject.data.dto.PreferenceProvider
import com.dolgantsev.androindfirstproject.di.AppComponent
import com.dolgantsev.androindfirstproject.di.DaggerAppComponent
import com.dolgantsev.androindfirstproject.di.moduls.DomainModule
import com.dolgantsev.androindfirstproject.di.moduls.RemoteModule


class App : Application() {
    lateinit var dagger: AppComponent
    lateinit var preferences: PreferenceProvider

    override fun onCreate() {
        super.onCreate()
        instance = this
        //Создаем компонент
        dagger = DaggerAppComponent.builder()
            .remoteModule(RemoteModule())
            .domainModule(DomainModule(this))
            .build()
        preferences = PreferenceProvider(this)
    }


    companion object {
        lateinit var instance: App
            private set
    }
}