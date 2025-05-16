package com.dolgantsev.androindfirstproject.view.fragments

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.dolgantsev.androindfirstproject.R
import com.dolgantsev.androindfirstproject.databinding.FragmentDetailsBinding
import com.dolgantsev.androindfirstproject.domain.Film
import com.dolgantsev.androindfirstproject.viewmodel.DetailsFragmentViewModel
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import android.provider.MediaStore
import android.provider.Settings
import com.dolgantsev.androindfirstproject.utils.NotificationReceiver
import java.util.*

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
                .load(com.dolgantsev.androindfirstproject.network.api.ApiConstants.IMAGES_URL + "w780" + film.posterPath)
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
                        { _ ->
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
                        { _ ->
                            Snackbar.make(binding.root, "Ошибка обновления сохраненного", Snackbar.LENGTH_SHORT).show()
                        }
                    )
                    .also { disposables.add(it) }
            }

            binding.detailsFabShare.setOnClickListener {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Обрати внимание: ${film.title}\n\n${film.overview}")
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(intent, "Поделиться:"))
            }

            binding.detailsFabDownloadWp.setOnClickListener {
                performAsyncLoadOfPoster(film)
            }

            binding.detailsFabNotify.setOnClickListener {
                showTimePickerDialog(film)
            }

            viewModel.getFilmById(film.id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { loadedFilm ->
                        binding.detailsDescription.text = loadedFilm.overview
                    },
                    { _ ->
                        Snackbar.make(binding.root, "Ошибка загрузки фильма", Snackbar.LENGTH_SHORT).show()
                    },
                    {
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

    private fun saveToGallery(bitmap: Bitmap, film: Film?) {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, film?.title?.handleSingleQuote() ?: "film_poster")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
            put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/FilmsSearchApp")
        }

        val resolver = requireActivity().contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            try {
                resolver.openOutputStream(it)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
                Snackbar.make(
                    binding.root,
                    R.string.downloaded_to_gallery,
                    Snackbar.LENGTH_LONG
                )
                    .setAction(R.string.open) {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(uri, "image/*")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        startActivity(intent)
                    }
                    .show()
            } catch (e: Exception) {
                Snackbar.make(binding.root, "Ошибка сохранения изображения", Snackbar.LENGTH_SHORT).show()
            }
        } ?: run {
            Snackbar.make(binding.root, "Не удалось сохранить изображение", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun performAsyncLoadOfPoster(film: Film?) {
        binding.progressBar.isVisible = true
        viewModel.loadWallpaper(
            com.dolgantsev.androindfirstproject.network.api.ApiConstants.IMAGES_URL + "original" + film?.posterPath,
            requireContext()
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { bitmap ->
                    saveToGallery(bitmap, film)
                    binding.progressBar.isVisible = false
                },
                { _ ->
                    binding.progressBar.isVisible = false
                    Snackbar.make(binding.root, "Ошибка загрузки изображения", Snackbar.LENGTH_SHORT).show()
                }
            )
            .also { disposables.add(it) }
    }

    private fun String.handleSingleQuote(): String {
        return this.replace("'", "")
    }

    private fun showTimePickerDialog(film: Film?) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            scheduleNotification(film, selectedHour, selectedMinute)
        }, hour, minute, true).show()
    }

    private fun scheduleNotification(film: Film?, hour: Int, minute: Int) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), NotificationReceiver::class.java).apply {
            putExtra("filmId", film?.id)
            action = "com.dolgantsev.androindfirstproject.NOTIFY"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            film?.id ?: 0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
                Snackbar.make(binding.root, "Напоминание запланировано на ${hour}:${minute}", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(
                    binding.root,
                    "Требуется разрешение для точных напоминаний",
                    Snackbar.LENGTH_LONG
                ).setAction("Разрешить") {
                    startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                }.show()
            }
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
            Snackbar.make(binding.root, "Напоминание запланировано на ${hour}:${minute}", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
        _binding = null
    }
}