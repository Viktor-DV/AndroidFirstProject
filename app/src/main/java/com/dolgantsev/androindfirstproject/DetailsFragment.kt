package com.dolgantsev.androindfirstproject

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment

class DetailsFragment : Fragment() {

    private lateinit var titleView: TextView
    private lateinit var posterView: ImageView
    private lateinit var descriptionView: TextView
    private lateinit var detailsFabFavorites: ImageView
    private lateinit var detailsFabShare: ImageView // добавим переменную для кнопки "Share"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация элементов UI
        titleView = view.findViewById(R.id.title)
        posterView = view.findViewById(R.id.poster)
        descriptionView = view.findViewById(R.id.description)
        detailsFabFavorites = view.findViewById(R.id.details_fab_favorites)
        detailsFabShare = view.findViewById(R.id.details_fab_share) // инициализация кнопки "Share"

        // Получаем объект Film из аргументов
        val film: Film? = arguments?.getParcelable("film", Film::class.java)

        if (film != null) {
            // Устанавливаем данные фильма
            titleView.text = film.title
            posterView.setImageResource(film.poster)
            descriptionView.text = film.description

            // Обновление иконки в зависимости от состояния избранного
            updateFavoriteIcon(film.isInFavorites)

            // Устанавливаем обработчик нажатия для кнопки "Избранное"
            detailsFabFavorites.setOnClickListener {
                film.isInFavorites = !film.isInFavorites
                updateFavoriteIcon(film.isInFavorites)
            }

            // Обработчик нажатия кнопки "Share"
            detailsFabShare.setOnClickListener {
                // Создаем интент
                val intent = Intent()
                // Указываем action с которым он запускается
                intent.action = Intent.ACTION_SEND
                // Кладем данные о нашем фильме
                intent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Check out this film: ${film.title} \n\n ${film.description}"
                )
                // Указываем MIME тип, чтобы система знала, какое приложение предложить
                intent.type = "text/plain"
                // Запускаем активити с выбором приложения для шаринга
                startActivity(Intent.createChooser(intent, "Share To:"))
            }
        } else {
            // Если фильм не найден, отображаем ошибку
            titleView.text = getString(R.string.error_no_film)
        }
    }

    // Метод для обновления иконки в зависимости от состояния "Избранное"
    private fun updateFavoriteIcon(isInFavorites: Boolean) {
        val iconResId = if (isInFavorites) {
            R.drawable.ic_baseline_favorite_24
        } else {
            R.drawable.ic_baseline_favorite_border_24
        }
        detailsFabFavorites.setImageResource(iconResId)
    }
}
