package com.dolgantsev.androindfirstproject.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.dolgantsev.androindfirstproject.domain.Film
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

@Dao
interface FilmDao {
    @Query("SELECT * FROM cached_films")
    fun getCachedFilms(): Single<List<Film>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Transaction
    fun insertAll(list: List<Film>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(film: Film): Completable

    @Update
    fun update(film: Film): Completable

    @Query("DELETE FROM cached_films WHERE id = :id")
    fun deleteById(id: Int): Completable

    @Query("SELECT * FROM cached_films WHERE rating >= :minRating")
    fun getFilmsByRating(minRating: Double): Single<List<Film>>

    @Query("SELECT * FROM cached_films WHERE id = :id LIMIT 1")
    fun getFilmById(id: Int): Maybe<Film>
}