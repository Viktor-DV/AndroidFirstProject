package com.dolgantsev.androindfirstproject.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dolgantsev.androindfirstproject.data.dao.FilmDao
import com.dolgantsev.androindfirstproject.domain.Film

@Database(entities = [Film::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun filmDao(): FilmDao
}