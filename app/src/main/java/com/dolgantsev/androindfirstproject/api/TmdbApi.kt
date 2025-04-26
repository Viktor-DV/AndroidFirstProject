package com.dolgantsev.androindfirstproject.api

import com.dolgantsev.androindfirstproject.data.dto.TmdbResultsDto
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApi {
    @GET("3/movie/popular")
    fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): Single<TmdbResultsDto>
}