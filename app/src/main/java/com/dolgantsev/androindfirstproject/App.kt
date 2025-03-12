package com.dolgantsev.androindfirstproject

import android.app.Application
import com.dolgantsev.androindfirstproject.di.AppComponent
import com.dolgantsev.androindfirstproject.di.DaggerAppComponent


class App : Application() {
    lateinit var dagger: AppComponent

    override fun onCreate() {
        super.onCreate()
        instance = this
        //Создаем компонент
        dagger = DaggerAppComponent.create()
    }

    companion object {
        lateinit var instance: App
            private set
    }
}