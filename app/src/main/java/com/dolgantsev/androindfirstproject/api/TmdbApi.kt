package com.dolgantsev.androindfirstproject.api

import com.dolgantsev.androindfirstproject.data.dto.TmdbResultsDto
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApi {
    @GET("3/movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): TmdbResultsDto
}