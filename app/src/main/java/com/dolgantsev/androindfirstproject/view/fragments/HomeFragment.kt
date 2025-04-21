package com.dolgantsev.androindfirstproject.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dolgantsev.androindfirstproject.MainActivity
import com.dolgantsev.androindfirstproject.databinding.FragmentHomeBinding
import com.dolgantsev.androindfirstproject.domain.Film
import com.dolgantsev.androindfirstproject.utils.AnimationHelper
import com.dolgantsev.androindfirstproject.view.rv_adapters.FilmListRecyclerAdapter
import com.dolgantsev.androindfirstproject.viewmodel.HomeFragmentViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeFragmentViewModel by viewModels()
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

        filmsAdapter = FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
            override fun click(film: Film) {
                (requireActivity() as MainActivity).launchDetailsFragment(film)
            }
        })

        binding.mainRecycler.apply {
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // Наблюдаем за прогресс-баром
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.showProgressBar.collect { isVisible ->
                    binding.progressBar.isVisible = isVisible
                }
            }
        }

        // Наблюдаем за списком фильмов
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filmsList.collect { films ->
                    filmsAdapter.submitList(films)
                }
            }
        }

        // Наблюдаем за ошибками
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorEvent.observe(viewLifecycleOwner) { errorMessage ->
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

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
                return false
            }
        })

        binding.pullToRefresh.setOnRefreshListener {
            viewModel.getFilms()
            binding.pullToRefresh.isRefreshing = false
        }

        binding.highRatedButton.setOnClickListener {
            viewModel.getHighRatedFilms()
        }

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

        binding.deleteButton.setOnClickListener {
            val currentFilms = filmsAdapter.getCurrentList()
            if (currentFilms.isNotEmpty()) {
                viewModel.deleteFilm(currentFilms[0].title)
            }
        }

        viewModel.getFilms()
    }

    fun updateMoviesList() {
        viewModel.getFilms()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}