package com.dolgantsev.androindfirstproject.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dolgantsev.androindfirstproject.domain.Film

@Dao
interface FilmDao {
    @Query("SELECT * FROM cached_films")
    fun getCachedFilms(): List<Film>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<Film>): LongArray

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(film: Film): Long

    @Update
    fun update(film: Film): Int

    @Query("DELETE FROM cached_films WHERE title = :title")
    fun deleteByTitle(title: String): Int

    @Query("SELECT * FROM cached_films WHERE rating >= :minRating")
    fun getFilmsByRating(minRating: Double): List<Film>

    @Query("SELECT * FROM cached_films WHERE title = :title LIMIT 1")
    fun getFilmByTitle(title: String): Film?
}