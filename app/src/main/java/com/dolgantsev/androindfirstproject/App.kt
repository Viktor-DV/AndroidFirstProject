package com.dolgantsev.androindfirstproject

import android.app.Application
import com.dolgantsev.androindfirstproject.data.dto.PreferenceProvider
import com.dolgantsev.androindfirstproject.di.AppComponent
import com.dolgantsev.androindfirstproject.di.AppDependencies
import com.dolgantsev.androindfirstproject.di.DaggerAppComponent

class App : Application() {

    lateinit var dagger: AppComponent
    lateinit var preferences: PreferenceProvider // Добавляем свойство preferences

    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        preferences = PreferenceProvider(this) // Инициализируем PreferenceProvider
        dagger = DaggerAppComponent.builder()
            .appDependencies(object : AppDependencies {
                override fun context() = this@App
            })
            .build()
    }
}