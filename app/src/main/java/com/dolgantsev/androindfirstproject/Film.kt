package com.dolgantsev.androindfirstproject

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Film(
    val title: String,
    val poster: Int,
    val description: String,
    var isInFavorites: Boolean = false
) : Parcelable
