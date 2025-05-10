package com.dolgantsev.androindfirstproject.view.rv_viewholders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dolgantsev.androindfirstproject.R
import com.dolgantsev.androindfirstproject.view.customviews.RatingDonutView

class FilmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val title: TextView = itemView.findViewById(R.id.title)
    private val poster: ImageView = itemView.findViewById(R.id.poster)
    private val overview: TextView = itemView.findViewById(R.id.overview)
    private val ratingDonut: RatingDonutView = itemView.findViewById(R.id.rating_donut)

    fun bind(film: com.dolgantsev.androindfirstproject.domain.Film) {
        title.text = film.title
        overview.text = film.overview
        Glide.with(itemView)
            .load(com.dolgantsev.androindfirstproject.network.api.ApiConstants.IMAGES_URL + "w342" + film.posterPath)
            .centerCrop()
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_error)
            .into(poster)

        // Анимация рейтинга при каждом bind
        ratingDonut.setProgress(0)
        ratingDonut.setProgress((film.rating * 10).toInt(), animate = true)
    }
}