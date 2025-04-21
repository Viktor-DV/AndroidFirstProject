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
    suspend fun getCachedFilms(): List<Film>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<Film>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(film: Film)

    @Update
    suspend fun update(film: Film)

    @Query("DELETE FROM cached_films WHERE title = :title")
    suspend fun deleteByTitle(title: String)

    @Query("SELECT * FROM cached_films WHERE rating >= :minRating")
    suspend fun getFilmsByRating(minRating: Double): List<Film>

    @Query("SELECT * FROM cached_films WHERE title = :title LIMIT 1")
    suspend fun getFilmByTitle(title: String): Film?
}