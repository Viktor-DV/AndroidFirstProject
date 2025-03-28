package com.dolgantsev.androindfirstproject.data.dto

import android.content.ContentValues
import android.database.Cursor
import com.dolgantsev.androindfirstproject.data.db.DatabaseHelper
import com.dolgantsev.androindfirstproject.domain.Film

class MainRepository(databaseHelper: DatabaseHelper) {
    private val sqlDb = databaseHelper.writableDatabase // Используем writableDatabase для записи и обновления

    fun putToDb(film: Film) {
        val cv = ContentValues().apply {
            put(DatabaseHelper.COLUMN_TITLE, film.title)
            put(DatabaseHelper.COLUMN_POSTER, film.poster)
            put(DatabaseHelper.COLUMN_DESCRIPTION, film.description)
            put(DatabaseHelper.COLUMN_RATING, film.rating)
        }
        sqlDb.insertWithOnConflict(DatabaseHelper.TABLE_NAME, null, cv, android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE)
    }

    fun getAllFromDB(): List<Film> {
        val cursor: Cursor = sqlDb.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_NAME}", null)
        val result = mutableListOf<Film>()
        if (cursor.moveToFirst()) {
            do {
                val title = cursor.getString(1)
                val poster = cursor.getString(2)
                val description = cursor.getString(3)
                val rating = cursor.getDouble(4)
                result.add(Film(title, poster, description, rating))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return result
    }

    // Метод для обновления фильма по названию
    fun updateFilm(film: Film) {
        val cv = ContentValues().apply {
            put(DatabaseHelper.COLUMN_POSTER, film.poster)
            put(DatabaseHelper.COLUMN_DESCRIPTION, film.description)
            put(DatabaseHelper.COLUMN_RATING, film.rating)
        }
        sqlDb.update(
            DatabaseHelper.TABLE_NAME,
            cv,
            "${DatabaseHelper.COLUMN_TITLE} = ?",
            arrayOf(film.title)
        )
    }

    // Метод для удаления фильма по названию
    fun deleteFilm(title: String) {
        sqlDb.delete(
            DatabaseHelper.TABLE_NAME,
            "${DatabaseHelper.COLUMN_TITLE} = ?",
            arrayOf(title)
        )
    }

    // Метод для получения фильмов с рейтингом выше заданного
    fun getFilmsByRating(minRating: Double): List<Film> {
        val cursor: Cursor = sqlDb.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_NAME} WHERE ${DatabaseHelper.COLUMN_RATING} >= ?",
            arrayOf(minRating.toString())
        )
        val result = mutableListOf<Film>()
        if (cursor.moveToFirst()) {
            do {
                val title = cursor.getString(1)
                val poster = cursor.getString(2)
                val description = cursor.getString(3)
                val rating = cursor.getDouble(4)
                result.add(Film(title, poster, description, rating))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return result
    }

    // Метод для получения фильма по названию
    fun getFilmByTitle(title: String): Film? {
        val cursor: Cursor = sqlDb.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_NAME} WHERE ${DatabaseHelper.COLUMN_TITLE} = ?",
            arrayOf(title)
        )
        var film: Film? = null
        if (cursor.moveToFirst()) {
            val filmTitle = cursor.getString(1)
            val poster = cursor.getString(2)
            val description = cursor.getString(3)
            val rating = cursor.getDouble(4)
            film = Film(filmTitle, poster, description, rating)
        }
        cursor.close()
        return film
    }
}