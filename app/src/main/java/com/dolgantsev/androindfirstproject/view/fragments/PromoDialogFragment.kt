package com.dolgantsev.androindfirstproject.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.dolgantsev.androindfirstproject.MainActivity
import com.dolgantsev.androindfirstproject.R
import com.dolgantsev.androindfirstproject.databinding.PromoDialogBinding
import com.dolgantsev.androindfirstproject.domain.Film
import com.dolgantsev.androindfirstproject.utils.TrialManager

// Класс для отображения промо-диалога с постером и описанием фильма
class PromoDialogFragment : DialogFragment() {

    private var _binding: PromoDialogBinding? = null
    // Ленивое получение binding для избежания утечек памяти
    private val binding get() = _binding!!

    private var posterUrl: String? = null  // URL постера из Remote Config
    private var film: Film? = null  // Объект фильма для отображения описания

    companion object {
        private const val ARG_POSTER_URL = "poster_url"  // Ключ для URL постера в Bundle
        private const val ARG_FILM = "film"  // Ключ для объекта фильма в Bundle

        // Фабричный метод для создания экземпляра диалога с параметрами
        fun newInstance(posterUrl: String, film: Film?): PromoDialogFragment {
            val fragment = PromoDialogFragment()
            val args = Bundle().apply {
                putString(ARG_POSTER_URL, posterUrl)
                putParcelable(ARG_FILM, film)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Извлечение параметров из аргументов с приведением типов для совместимости
        posterUrl = arguments?.getString(ARG_POSTER_URL)
        @Suppress("DEPRECATION")  // Подавляем предупреждение об устаревшем методе
        film = arguments?.getParcelable(ARG_FILM) as? Film  // Безопасное приведение типов
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Инициализация binding для разметки диалога
        _binding = PromoDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Загрузка постера с помощью Glide, если URL не пустой
        if (!posterUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(posterUrl)
                .placeholder(R.drawable.placeholder)  // Заглушка при загрузке
                .error(R.drawable.error)  // Изображение при ошибке
                .into(binding.promoPoster)
        }

        // Установка текста описания из объекта фильма или заглушки
        binding.promoDescription.text = film?.overview ?: "Описание недоступно"

        // Обработчик нажатия на кнопку "Подробнее"
        binding.promoDetailsButton.setOnClickListener {
            film?.let { film ->
                (activity as? MainActivity)?.launchDetailsFragment(film)
            }
            TrialManager.markPromoDialogShown(requireContext())  // Отмечаем показ диалога
            dismiss()  // Закрываем диалог
        }

        // Обработчик нажатия на кнопку "Не интересует"
        binding.promoDismissButton.setOnClickListener {
            TrialManager.markPromoDialogShown(requireContext())  // Отмечаем показ диалога
            dismiss()  // Закрываем диалог
        }
    }

    override fun onStart() {
        super.onStart()
        // Установка ширины диалога на 90% экрана
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Очистка binding для предотвращения утечек памяти
        _binding = null
    }
}