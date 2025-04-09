package com.dolgantsev.androindfirstproject.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dolgantsev.androindfirstproject.domain.Film

@Dao
interface FilmDao {
    // Получаем все фильмы
    @Query("SELECT * FROM cached_films")
    fun getCachedFilms(): LiveData<List<Film>>

    // Вставляем список фильмов, при конфликте заменяем
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<Film>)

    // Вставляем один фильм
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(film: Film)

    // Обновляем фильм
    @Update
    fun update(film: Film)

    // Удаляем фильм по названию
    @Query("DELETE FROM cached_films WHERE title = :title")
    fun deleteByTitle(title: String)

    // Получаем фильмы по рейтингу
    @Query("SELECT * FROM cached_films WHERE rating >= :minRating")
    fun getFilmsByRating(minRating: Double): List<Film>

    // Получаем фильм по названию
    @Query("SELECT * FROM cached_films WHERE title = :title LIMIT 1")
    fun getFilmByTitle(title: String): Film?
}
