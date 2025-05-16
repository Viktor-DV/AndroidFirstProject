package com.dolgantsev.androindfirstproject

import android.app.Application
import com.dolgantsev.androindfirstproject.core.dto.PreferenceProvider
import com.dolgantsev.androindfirstproject.di.AppComponent
import com.dolgantsev.androindfirstproject.di.DaggerAppComponent

class App : Application() {

    lateinit var appComponent: AppComponent
        private set

    val preferences: PreferenceProvider by lazy { PreferenceProvider(applicationContext) }

    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        appComponent = DaggerAppComponent.factory().create(applicationContext)
    }
}
