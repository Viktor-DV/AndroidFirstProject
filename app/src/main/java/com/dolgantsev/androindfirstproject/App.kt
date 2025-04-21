package com.dolgantsev.androindfirstproject

import android.app.Application
import androidx.room.Room
import com.dolgantsev.androindfirstproject.data.db.AppDatabase
import com.dolgantsev.androindfirstproject.data.dto.PreferenceProvider
import com.dolgantsev.androindfirstproject.di.AppComponent
import com.dolgantsev.androindfirstproject.di.DaggerAppComponent
import com.dolgantsev.androindfirstproject.di.moduls.DomainModule
import com.dolgantsev.androindfirstproject.di.moduls.RemoteModule

class App : Application() {
    lateinit var dagger: AppComponent
    lateinit var preferences: PreferenceProvider
    lateinit var db: AppDatabase

    override fun onCreate() {
        super.onCreate()
        instance = this
        // Инициализация базы данных
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "films.db")
            .addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3)
            .build()
        // Создаем компонент
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