package com.dolgantsev.androindfirstproject

import android.app.Application
import com.dolgantsev.androindfirstproject.data.dto.PreferenceProvider
import com.dolgantsev.androindfirstproject.di.AppComponent
import com.dolgantsev.androindfirstproject.di.AppDependencies
import com.dolgantsev.androindfirstproject.di.DaggerAppComponent

class App : Application() {

    lateinit var dagger: AppComponent
    val preferences: PreferenceProvider by lazy { PreferenceProvider(applicationContext) } // Используем lazy

    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        dagger = DaggerAppComponent.builder()
            .appDependencies(object : AppDependencies {
                override fun context() = applicationContext // Используем applicationContext
            })
            .build()
    }
}