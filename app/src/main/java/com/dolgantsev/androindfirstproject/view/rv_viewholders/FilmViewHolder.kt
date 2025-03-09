package com.dolgantsev.androindfirstproject.view.rv_viewholders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dolgantsev.androindfirstproject.R
import com.dolgantsev.androindfirstproject.api.ApiConstants
import com.dolgantsev.androindfirstproject.domain.Film
import com.dolgantsev.androindfirstproject.view.customviews.RatingDonutView

class FilmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    // Привязываем View из layout к переменным
    private val title: TextView = itemView.findViewById(R.id.title)
    private val poster: ImageView = itemView.findViewById(R.id.poster)
    private val description: TextView = itemView.findViewById(R.id.description)
    private val ratingDonut: RatingDonutView = itemView.findViewById(R.id.rating_donut) // Исправлен тип на RatingDonutView

    // Флаг для отслеживания, была ли анимация уже запущена
    private var isAnimated = false

    // В этом методе кладем данные из Film в наши View
    fun bind(film: Film) {
        // Устанавливаем заголовок
        title.text = film.title
        // Устанавливаем описание
        description.text = film.description
        // Указываем контейнер, в котором будет "жить" наша картинка
        Glide.with(itemView)
            .load(ApiConstants.IMAGES_URL + "w342" + film.poster)
            .centerCrop()
            .into(poster)

        // Настраиваем рейтинг с анимацией
        if (!isAnimated) {
            ratingDonut.setProgress(0) // Сбрасываем прогресс перед анимацией
            ratingDonut.setProgress((film.rating * 10).toInt(), animate = true) // Запускаем анимацию один раз
            isAnimated = true // Устанавливаем флаг, чтобы анимация не повторялась
        } else {
            ratingDonut.setProgress((film.rating * 10).toInt(), animate = false) // Без анимации при повторном bind
        }
    }
}