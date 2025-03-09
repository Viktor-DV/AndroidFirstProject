package com.dolgantsev.androindfirstproject.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.dolgantsev.androindfirstproject.R
import com.dolgantsev.androindfirstproject.api.ApiConstants
import com.dolgantsev.androindfirstproject.databinding.FragmentDetailsBinding // Импорт binding
import com.dolgantsev.androindfirstproject.domain.Film

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val film: Film? = arguments?.getParcelable("film")

        if (film != null) {
            binding.detailsDescription.text = film.description // ← details_description

            // Загрузка постера в details_poster
            Glide.with(this)
                .load(ApiConstants.IMAGES_URL + "w780" + film.poster)
                .centerCrop()
                .into(binding.detailsPoster) // ← details_poster

            // Обновление иконки избранного
            updateFavoriteIcon(film.isInFavorites)

            // Кнопка "Избранное"
            binding.detailsFabFavorites.setOnClickListener {
                film.isInFavorites = !film.isInFavorites
                updateFavoriteIcon(film.isInFavorites)
            }

            // Кнопка "Share"
            binding.detailsFabShare.setOnClickListener {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Check out this film: ${film.title}\n\n${film.description}")
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(intent, "Share To:"))
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}