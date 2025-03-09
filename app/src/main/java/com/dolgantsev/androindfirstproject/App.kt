package com.dolgantsev.androindfirstproject

import android.app.Application
import com.dolgantsev.androindfirstproject.api.ApiConstants
import com.dolgantsev.androindfirstproject.api.TmdbApi
import com.dolgantsev.androindfirstproject.domain.Interactor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class App : Application() {

    lateinit var interactor: Interactor

    override fun onCreate() {
        super.onCreate()
        instance = this
        // Инициализируем интерактор с сервисом API
        interactor = Interactor(createTmdbApi())
    }

    companion object {
        lateinit var instance: App
            private set
    }

    private fun createTmdbApi(): TmdbApi {
        val okHttpClient = OkHttpClient.Builder()
            .callTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BASIC
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            })
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        return retrofit.create(TmdbApi::class.java)
    }
}