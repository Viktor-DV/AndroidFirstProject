package com.dolgantsev.androindfirstproject.network.dto

import com.google.gson.annotations.SerializedName

data class TmdbResultsDto(
    val page: Int,
    @SerializedName("total_results") val totalResults: Int,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("results") val results: List<TmdbFilm>
)