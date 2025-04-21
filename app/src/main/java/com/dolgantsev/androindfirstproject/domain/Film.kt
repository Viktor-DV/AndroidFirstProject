package com.dolgantsev.androindfirstproject.domain

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "cached_films", indices = [Index(value = ["title"], unique = true)])
data class Film(
    @PrimaryKey val id: Int, // Убрали autoGenerate = true
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "poster_path") val poster: String,
    @ColumnInfo(name = "overview") val description: String,
    @ColumnInfo(name = "rating") var rating: Double = 0.0,
    @ColumnInfo(name = "is_in_favorites") var isInFavorites: Boolean = false,
    @ColumnInfo(name = "is_saved") var isSaved: Boolean = false
) : Parcelable