package com.dolgantsev.androindfirstproject.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dolgantsev.androindfirstproject.MainActivity
import com.dolgantsev.androindfirstproject.databinding.FragmentHomeBinding
import com.dolgantsev.androindfirstproject.domain.Film
import com.dolgantsev.androindfirstproject.utils.AnimationHelper
import com.dolgantsev.androindfirstproject.view.rv_adapters.FilmListRecyclerAdapter
import com.dolgantsev.androindfirstproject.viewmodel.HomeFragmentViewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by lazy {
        ViewModelProvider(this).get(HomeFragmentViewModel::class.java)
    }
    private lateinit var filmsAdapter: FilmListRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Анимация
        AnimationHelper.performFragmentCircularRevealAnimation(binding.root, 1)

        // Инициализация адаптера
        filmsAdapter = FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
            override fun click(film: Film) {
                (requireActivity() as MainActivity).launchDetailsFragment(film)
            }
        })

        // Настройка RecyclerView
        binding.mainRecycler.apply {
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // Наблюдение за списком фильмов
        viewModel.filmsListLiveData.observe(viewLifecycleOwner, Observer { films ->
            filmsAdapter.addItems(films)
        })

        // Обработка поиска по названию через SearchView
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.isNotEmpty()) {
                        viewModel.getFilmByTitle(it)
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false // Пока не фильтруем в реальном времени
            }
        })

        // Обновление списка при свайпе
        binding.pullToRefresh.setOnRefreshListener {
            viewModel.getFilms()
            binding.pullToRefresh.isRefreshing = false
        }

        // Кнопка для фильмов с высоким рейтингом
        binding.highRatedButton.setOnClickListener {
            viewModel.getHighRatedFilms()
        }

        // Кнопка для обновления фильма (пример с первым фильмом из списка)
        binding.updateButton.setOnClickListener {
            val currentFilms = filmsAdapter.getCurrentList()
            if (currentFilms.isNotEmpty()) {
                val filmToUpdate = currentFilms[0].copy(
                    poster = "updated_poster_url",
                    description = "Updated description",
                    rating = 8.5
                )
                viewModel.updateFilm(filmToUpdate)
            }
        }

        // Кнопка для удаления фильма (пример с первым фильмом из списка)
        binding.deleteButton.setOnClickListener {
            val currentFilms = filmsAdapter.getCurrentList()
            if (currentFilms.isNotEmpty()) {
                viewModel.deleteFilm(currentFilms[0].title)
            }
        }

        // Начальная загрузка фильмов
        viewModel.getFilms()
    }

    // Добавленный метод updateMoviesList
    fun updateMoviesList() {
        viewModel.getFilms() // Обновляет список фильмов через ViewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Освобождение памяти
    }
}