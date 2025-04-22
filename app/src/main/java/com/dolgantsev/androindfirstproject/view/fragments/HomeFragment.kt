package com.dolgantsev.androindfirstproject.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dolgantsev.androindfirstproject.App
import com.dolgantsev.androindfirstproject.MainActivity
import com.dolgantsev.androindfirstproject.databinding.FragmentHomeBinding
import com.dolgantsev.androindfirstproject.domain.Film
import com.dolgantsev.androindfirstproject.view.rv_adapters.FilmListRecyclerAdapter
import com.dolgantsev.androindfirstproject.view.rv_adapters.TopSpaceItemDecoration
import com.dolgantsev.androindfirstproject.viewmodel.HomeFragmentViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var filmsAdapter: FilmListRecyclerAdapter
    private val viewModel by lazy {
        ViewModelProvider(this)[HomeFragmentViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        App.instance.dagger.inject(viewModel)

        initRecyclerView()

        // Наблюдаем за списком фильмов
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.filmsList.collect { films ->
                filmsAdapter.addItems(films)
            }
        }

        // Наблюдаем за прогресс-баром
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.showProgressBar.collect { isVisible ->
                binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
            }
        }

        // Наблюдаем за ошибками
        viewModel.errorEvent.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
        }

        // Обработка поиска
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Здесь можно реализовать поиск фильмов по запросу
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Можно реализовать фильтрацию списка фильмов в реальном времени
                return true
            }
        })

        // Обработка обновления через SwipeRefreshLayout
        binding.pullToRefresh.setOnRefreshListener {
            viewModel.getFilms()
            binding.pullToRefresh.isRefreshing = false
        }

        // Обработка кнопок
        binding.highRatedButton.setOnClickListener {
            // Фильтрация фильмов с высоким рейтингом (например, рейтинг > 7)
            Toast.makeText(requireContext(), "Фильтрация по высокому рейтингу", Toast.LENGTH_SHORT).show()
        }

        binding.updateButton.setOnClickListener {
            viewModel.getFilms()
        }

        binding.deleteButton.setOnClickListener {
            // Удаление фильмов (например, очистка кэша)
            Toast.makeText(requireContext(), "Удаление фильмов", Toast.LENGTH_SHORT).show()
        }

        viewModel.getFilms()
    }

    private fun initRecyclerView() {
        binding.mainRecycler.apply {
            filmsAdapter = FilmListRecyclerAdapter(
                object : FilmListRecyclerAdapter.OnItemClickListener {
                    override fun click(film: Film) {
                        (requireActivity() as MainActivity).launchDetailsFragment(film)
                    }
                }
            )
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            val decorator = TopSpaceItemDecoration(8)
            addItemDecoration(decorator)
        }
    }

    fun updateMoviesList() {
        viewModel.getFilms()
    }
}