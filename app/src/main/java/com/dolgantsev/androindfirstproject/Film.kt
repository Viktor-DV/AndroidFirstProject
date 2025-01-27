package com.dolgantsev.androindfirstproject

import android.os.Parcelable

@Parcelize
data class Film(
    val title: String,
    val poster: Int,
    val description: String
) : Parcelable

