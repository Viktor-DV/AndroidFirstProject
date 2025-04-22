package com.dolgantsev.androindfirstproject.viewmodel

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.dolgantsev.androindfirstproject.App
import com.dolgantsev.androindfirstproject.domain.Film
import com.dolgantsev.androindfirstproject.domain.Interactor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DetailsFragmentViewModel : ViewModel() {

    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
    }

    suspend fun updateFilm(film: Film) {
        interactor.updateFilm(film)
    }

    suspend fun loadWallpaper(url: String, context: Context): Bitmap = withContext(Dispatchers.IO) {
        Glide.with(context)
            .asBitmap()
            .load(url)
            .submit()
            .get()
    }
}