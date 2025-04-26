package com.dolgantsev.androindfirstproject.view.fragments

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.dolgantsev.androindfirstproject.R
import com.dolgantsev.androindfirstproject.api.ApiConstants
import com.dolgantsev.androindfirstproject.databinding.FragmentDetailsBinding
import com.dolgantsev.androindfirstproject.domain.Film
import com.dolgantsev.androindfirstproject.viewmodel.DetailsFragmentViewModel
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailsFragmentViewModel by viewModels()
    private val disposables = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val film: Film? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("film", Film::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable("film")
        }

        if (film != null) {
            binding.title.text = film.title
            binding.detailsDescription.text = film.overview

            Glide.with(this)
                .load(ApiConstants.IMAGES_URL + "w780" + film.posterPath)
                .centerCrop()
                .into(binding.detailsPoster)

            updateFavoriteIcon(film.isInFavorites)
            updateSavedIcon(film.isSaved)

            binding.detailsFabFavorites.setOnClickListener {
                film.isInFavorites = !film.isInFavorites
                updateFavoriteIcon(film.isInFavorites)
                viewModel.updateFilm(film)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { /* Успешное обновление, ничего не делаем */ },
                        { error ->
                            Snackbar.make(binding.root, "Ошибка обновления избранного", Snackbar.LENGTH_SHORT).show()
                        }
                    )
                    .also { disposables.add(it) }
            }

            binding.detailsFabSave.setOnClickListener {
                film.isSaved = !film.isSaved
                updateSavedIcon(film.isSaved)
                viewModel.updateFilm(film)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { /* Успешное обновление, ничего не делаем */ },
                        { error ->
                            Snackbar.make(binding.root, "Ошибка обновления сохраненного", Snackbar.LENGTH_SHORT).show()
                        }
                    )
                    .also { disposables.add(it) }
            }

            binding.detailsFabShare.setOnClickListener {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Check out this film: ${film.title}\n\n${film.overview}")
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(intent, "Share To:"))
            }

            binding.detailsFabDownloadWp.setOnClickListener {
                performAsyncLoadOfPoster(film)
            }

            // Вызов getFilmByTitle с использованием Maybe
            viewModel.getFilmByTitle(film.title)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { loadedFilm ->
                        // Фильм найден, обновляем UI
                        binding.detailsDescription.text = loadedFilm.overview
                    },
                    { error ->
                        // Ошибка при загрузке
                        Snackbar.make(binding.root, "Ошибка загрузки фильма", Snackbar.LENGTH_SHORT).show()
                    },
                    {
                        // Фильм не найден (onComplete)
                        binding.detailsDescription.text = "Фильм не найден в базе данных"
                    }
                )
                .also { disposables.add(it) }
        } else {
            binding.detailsDescription.text = getString(R.string.error_no_film)
        }
    }

    private fun updateFavoriteIcon(isInFavorites: Boolean) {
        val iconResId = if (isInFavorites) {
            R.drawable.ic_baseline_favorite_24
        } else {
            R.drawable.ic_baseline_favorite_border_24
        }
        binding.detailsFabFavorites.setImageResource(iconResId)
    }

    private fun updateSavedIcon(isSaved: Boolean) {
        val iconResId = if (isSaved) {
            R.drawable.ic_baseline_bookmark_24
        } else {
            R.drawable.ic_baseline_bookmark_border_24
        }
        binding.detailsFabSave.setImageResource(iconResId)
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )
    }

    private fun saveToGallery(bitmap: Bitmap, film: Film?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.TITLE, film?.title?.handleSingleQuote())
                put(MediaStore.Images.Media.DISPLAY_NAME, film?.title?.handleSingleQuote())
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
                put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/FilmsSearchApp")
            }
            val contentResolver = requireActivity().contentResolver
            val uri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            uri?.let {
                val outputStream = contentResolver.openOutputStream(it)
                outputStream?.let { stream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    stream.close()
                }
            }
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.insertImage(
                requireActivity().contentResolver,
                bitmap,
                film?.title?.handleSingleQuote(),
                film?.overview?.handleSingleQuote()
            )
        }
    }

    private fun String.handleSingleQuote(): String {
        return this.replace("'", "")
    }

    private fun performAsyncLoadOfPoster(film: Film?) {
        if (!checkPermission()) {
            requestPermission()
            return
        }
        binding.progressBar.isVisible = true
        viewModel.loadWallpaper(ApiConstants.IMAGES_URL + "original" + film?.posterPath, requireContext())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { bitmap ->
                    saveToGallery(bitmap, film)
                    Snackbar.make(
                        binding.root,
                        R.string.downloaded_to_gallery,
                        Snackbar.LENGTH_LONG
                    )
                        .setAction(R.string.open) {
                            val intent = Intent()
                            intent.action = Intent.ACTION_VIEW
                            intent.type = "image/*"
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        .show()
                    binding.progressBar.isVisible = false
                },
                { error ->
                    binding.progressBar.isVisible = false
                }
            )
            .also { disposables.add(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
        _binding = null
    }
}