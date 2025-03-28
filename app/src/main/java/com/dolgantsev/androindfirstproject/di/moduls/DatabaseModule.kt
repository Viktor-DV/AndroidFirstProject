package com.dolgantsev.androindfirstproject.di.moduls

import android.content.Context
import com.dolgantsev.androindfirstproject.data.db.DatabaseHelper
import com.dolgantsev.androindfirstproject.data.dto.MainRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabaseHelper(context: Context) = DatabaseHelper(context)

    @Provides
    @Singleton
    fun provideRepository(databaseHelper: DatabaseHelper) = MainRepository(databaseHelper)
}