package com.dolgantsev.androindfirstproject.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dolgantsev.androindfirstproject.App
import com.dolgantsev.androindfirstproject.MainActivity
import com.dolgantsev.androindfirstproject.databinding.FragmentHomeBinding
import com.dolgantsev.androindfirstproject.domain.Film
import com.dolgantsev.androindfirstproject.view.rv_adapters.FilmListRecyclerAdapter
import com.dolgantsev.androindfirstproject.view.rv_adapters.TopSpaceItemDecoration
import com.dolgantsev.androindfirstproject.viewmodel.HomeFragmentViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var filmsAdapter: FilmListRecyclerAdapter
    private val viewModel by lazy {
        ViewModelProvider(this)[HomeFragmentViewModel::class.java]
    }

    private val filmsDataBase = mutableListOf<Film>()

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

        viewModel.filmsListData.observe(viewLifecycleOwner) { films ->
            filmsDataBase.clear()
            filmsDataBase.addAll(films)
            filmsAdapter.notifyDataSetChanged()
        }

        viewModel.getFilms()
    }

    private fun initRecyclerView() {
        binding.mainRecycler.apply {
            filmsAdapter = FilmListRecyclerAdapter(
                filmsDataBase,
                object : FilmListRecyclerAdapter.OnItemClickListener {
                    override fun click(film: Film) {
                        (requireActivity() as MainActivity).launchDetailsFragment(film)
                    }
                },
                null
            )
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            val decorator = TopSpaceItemDecoration(8)
            addItemDecoration(decorator)
        }
    }

    // Добавляем метод для обновления списка фильмов
    fun updateMoviesList() {
        viewModel.getFilms()
    }
}