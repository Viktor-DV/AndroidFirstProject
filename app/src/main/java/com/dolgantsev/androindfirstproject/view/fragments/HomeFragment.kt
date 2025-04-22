package com.dolgantsev.androindfirstproject.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dolgantsev.androindfirstproject.App
import com.dolgantsev.androindfirstproject.MainActivity
import com.dolgantsev.androindfirstproject.databinding.FragmentHomeBinding
import com.dolgantsev.androindfirstproject.domain.Film
import com.dolgantsev.androindfirstproject.view.rv_adapters.FilmListRecyclerAdapter
import com.dolgantsev.androindfirstproject.view.rv_adapters.TopSpacingItemDecoration
import com.dolgantsev.androindfirstproject.viewmodel.HomeFragmentViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var filmsAdapter: FilmListRecyclerAdapter
    private val viewModel: HomeFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        App.instance.dagger.inject(viewModel)

        initRecyclerView()

        // Наблюдаем за списком фильмов
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.filmsList.collect { films ->
                    filmsAdapter.submitList(films)
                }
            }
        }

        // Наблюдаем за прогресс-баром
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.showProgressBar.collect { isVisible ->
                    binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
                }
            }
        }

        // Наблюдаем за ошибками
        viewModel.errorEvent.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
        }

        // Обработка поиска
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    viewModel.searchFilms(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    viewModel.searchFilms(newText)
                }
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
            viewModel.filterHighRatedFilms()
        }

        binding.updateButton.setOnClickListener {
            viewModel.getFilms()
        }

        binding.deleteButton.setOnClickListener {
            viewModel.clearFilms()
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
            val decorator = TopSpacingItemDecoration(8)
            addItemDecoration(decorator)
        }
    }

    fun updateMoviesList() {
        viewModel.getFilms()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}