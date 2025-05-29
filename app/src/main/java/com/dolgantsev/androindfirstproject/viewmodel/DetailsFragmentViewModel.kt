package com.dolgantsev.androindfirstproject.viewmodel

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.dolgantsev.androindfirstproject.core.interactors.Interactor
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class DetailsFragmentViewModel @Inject constructor(
    private val interactor: Interactor
) : ViewModel() {

    fun updateFilm(film: com.dolgantsev.androindfirstproject.domain.Film): Completable {
        return interactor.updateFilm(film)
    }

    fun loadWallpaper(url: String, context: Context): Single<Bitmap> {
        return Single.fromCallable {
            Glide.with(context)
                .asBitmap()
                .load(url)
                .submit()
                .get()
        }
    }

    fun getFilmById(id: Int): Maybe<com.dolgantsev.androindfirstproject.domain.Film> {
        return interactor.getFilmById(id)
    }
}