package com.dolgantsev.androindfirstproject.di.moduls

import android.content.Context
import androidx.room.Room
import com.dolgantsev.androindfirstproject.data.dao.FilmDao
import com.dolgantsev.androindfirstproject.data.db.AppDatabase
import com.dolgantsev.androindfirstproject.data.repository.MainRepository
import com.dolgantsev.androindfirstproject.domain.Interactor
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DomainModule {

    @Provides
    @Singleton
    fun provideAppDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "film_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideFilmDao(database: AppDatabase): FilmDao {
        return database.filmDao()
    }

    @Provides
    @Singleton
    fun provideInteractor(repository: MainRepository): Interactor {
        return Interactor(repository)
    }
}