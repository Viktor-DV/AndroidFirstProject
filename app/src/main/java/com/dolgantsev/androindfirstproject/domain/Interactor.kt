package com.dolgantsev.androindfirstproject.domain

import com.dolgantsev.androindfirstproject.api.APIKEY
import com.dolgantsev.androindfirstproject.api.TmdbApi
import com.dolgantsev.androindfirstproject.data.dto.TmdbResultsDto
import com.dolgantsev.androindfirstproject.utils.Converter
import com.dolgantsev.androindfirstproject.viewmodel.HomeFragmentViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Interactor(
    private val repo: MainRepository,
    private val retrofitService: TmdbApi
) {
    fun getFilmsFromApi(page: Int, callback: HomeFragmentViewModel.ApiCallback) {
        retrofitService.getFilms(APIKEY.KEY, "ru-RU", page).enqueue(object : Callback<TmdbResultsDto> {
            override fun onResponse(
                call: Call<TmdbResultsDto>,
                response: Response<TmdbResultsDto>
            ) {
                callback.onSuccess(
                    Converter.convertApiListToDtoList(response.body()?.tmdbFilms)
                )
            }

            override fun onFailure(call: Call<TmdbResultsDto>, t: Throwable) {
                callback.onFailure()
            }
        })
    }
}