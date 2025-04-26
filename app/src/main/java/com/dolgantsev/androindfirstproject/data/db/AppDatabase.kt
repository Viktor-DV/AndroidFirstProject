package com.dolgantsev.androindfirstproject.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dolgantsev.androindfirstproject.data.dao.FilmDao
import com.dolgantsev.androindfirstproject.domain.Film

@Database(entities = [Film::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun filmDao(): FilmDao
    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE cached_films ADD COLUMN is_saved INTEGER NOT NULL DEFAULT 0")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE cached_films_temp (
                        id INTEGER PRIMARY KEY NOT NULL,
                        title TEXT NOT NULL,
                        poster_path TEXT NOT NULL,
                        overview TEXT NOT NULL,
                        rating REAL NOT NULL DEFAULT 0.0,
                        is_in_favorites INTEGER NOT NULL DEFAULT 0,
                        is_saved INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())

                db.execSQL("""
                    INSERT INTO cached_films_temp (title, poster_path, overview, rating, is_in_favorites, is_saved)
                    SELECT title, poster_path, overview, rating, is_in_favorites, is_saved
                    FROM cached_films
                """.trimIndent())

                db.execSQL("DROP TABLE cached_films")
                db.execSQL("ALTER TABLE cached_films_temp RENAME TO cached_films")
                db.execSQL("CREATE UNIQUE INDEX index_cached_films_title ON cached_films (title)")
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "films_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}