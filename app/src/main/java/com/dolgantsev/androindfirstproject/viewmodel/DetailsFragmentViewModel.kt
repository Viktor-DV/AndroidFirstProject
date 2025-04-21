package com.dolgantsev.androindfirstproject.viewmodel

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.dolgantsev.androindfirstproject.data.dto.MainRepository
import com.dolgantsev.androindfirstproject.domain.Film
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DetailsFragmentViewModel : ViewModel() {

    private var repository: MainRepository? = null

    fun initRepository(repo: MainRepository) {
        repository = repo
    }

    suspend fun updateFilm(film: Film) {
        repository?.updateFilm(film)
    }

    suspend fun loadWallpaper(url: String, context: Context): Bitmap = withContext(Dispatchers.IO) {
        Glide.with(context)
            .asBitmap()
            .load(url)
            .submit()
            .get()
    }
}
