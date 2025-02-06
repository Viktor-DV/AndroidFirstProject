package com.dolgantsev.androindfirstproject

import FilmListRecyclerAdapter
import TopSpacingItemDecoration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class FavoritesFragment : Fragment() {

    private lateinit var filmsAdapter: FilmListRecyclerAdapter
    private lateinit var favoritesRecycler: RecyclerView // Добавляем RecyclerView для использования через findViewById

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Используем стандартный способ инфлейта разметки без DataBinding
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Находим RecyclerView вручную через findViewById
        favoritesRecycler = view.findViewById(R.id.favorites_recycler)

        // Инициализация списка избранных фильмов (например, из базы данных)
        val favoritesList: List<Film> = emptyList() // Замените на реальный список

        // Инициализация адаптера для RecyclerView
        filmsAdapter = FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
            override fun click(film: Film) {
                (requireActivity() as MainActivity).launchDetailsFragment(film)
            }
        })

        // Настройка RecyclerView
        favoritesRecycler.apply {
            // Присваиваем адаптер
            adapter = filmsAdapter

            // Присваиваем LayoutManager
            layoutManager = LinearLayoutManager(requireContext())

            // Применяем декоратор для отступов
            val decorator = TopSpacingItemDecoration(8)
            addItemDecoration(decorator)
        }

        // Добавляем элементы в адаптер
        filmsAdapter.addItems(favoritesList) // Убедитесь, что метод добавляет данные
    }
}
