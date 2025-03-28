package com.dolgantsev.androindfirstproject.domain

import com.dolgantsev.androindfirstproject.api.APIKEY
import com.dolgantsev.androindfirstproject.api.TmdbApi
import com.dolgantsev.androindfirstproject.data.dto.MainRepository
import com.dolgantsev.androindfirstproject.data.dto.PreferenceProvider
import com.dolgantsev.androindfirstproject.data.dto.TmdbResultsDto
import com.dolgantsev.androindfirstproject.utils.Converter
import com.dolgantsev.androindfirstproject.viewmodel.HomeFragmentViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Interactor(
    private val repo: MainRepository,
    private val retrofitService: TmdbApi,
    private val preferences: PreferenceProvider
) {
    fun getFilmsFromApi(page: Int, callback: HomeFragmentViewModel.ApiCallback) {
        retrofitService.getFilms(getDefaultCategoryFromPreferences(), APIKEY.KEY, "ru-RU", page)
            .enqueue(object : Callback<TmdbResultsDto> {
                override fun onResponse(call: Call<TmdbResultsDto>, response: Response<TmdbResultsDto>) {
                    val list = Converter.convertApiListToDtoList(response.body()?.tmdbFilms)
                    list.forEach { repo.putToDb(it) }
                    callback.onSuccess(list)
                }

                override fun onFailure(call: Call<TmdbResultsDto>, t: Throwable) {
                    callback.onFailure()
                }
            })
    }

    fun getFilmsFromDB(): List<Film> = repo.getAllFromDB()

    fun updateFilm(film: Film) = repo.updateFilm(film)

    fun deleteFilm(title: String) = repo.deleteFilm(title)

    fun getFilmsByRating(minRating: Double): List<Film> = repo.getFilmsByRating(minRating)

    fun getFilmByTitle(title: String): Film? = repo.getFilmByTitle(title)

    fun saveDefaultCategoryToPreferences(category: String) {
        preferences.saveDefaultCategory(category)
    }

    fun getDefaultCategoryFromPreferences(): String {
        return preferences.getDefaultCategory()
    }
}