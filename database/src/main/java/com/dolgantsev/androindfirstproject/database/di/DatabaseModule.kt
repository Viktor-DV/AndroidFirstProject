package com.dolgantsev.androindfirstproject.database.di

import android.content.Context
import com.dolgantsev.androindfirstproject.database.DatabaseSourceImpl
import com.dolgantsev.androindfirstproject.database.dao.FilmDao
import com.dolgantsev.androindfirstproject.database.db.AppDatabase
import com.dolgantsev.androindfirstproject.domain.DatabaseSource
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideFilmDao(database: AppDatabase): FilmDao {
        return database.filmDao()
    }

    @Provides
    @Singleton
    fun provideDatabaseSource(filmDao: FilmDao): DatabaseSource {
        return DatabaseSourceImpl(filmDao)
    }
}