package com.dolgantsev.androindfirstproject.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dolgantsev.androindfirstproject.MainActivity
import com.dolgantsev.androindfirstproject.databinding.FragmentSavedBinding
import com.dolgantsev.androindfirstproject.domain.Film
import com.dolgantsev.androindfirstproject.utils.AnimationHelper
import com.dolgantsev.androindfirstproject.view.rv_adapters.FilmListRecyclerAdapter
import com.dolgantsev.androindfirstproject.view.rv_adapters.TopSpacingItemDecoration
import com.dolgantsev.androindfirstproject.viewmodel.SavedFragmentViewModel

class SavedFragment : Fragment() {

    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SavedFragmentViewModel by viewModels()
    private lateinit var filmsAdapter: FilmListRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        filmsAdapter = FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
            override fun click(film: Film) {
                (requireActivity() as MainActivity).launchDetailsFragment(film)
            }
        })

        binding.savedRecycler.apply {
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(TopSpacingItemDecoration(8))
        }

        // Наблюдаем за LiveData
        viewModel.savedFilms.observe(viewLifecycleOwner) { films ->
            filmsAdapter.submitList(films)
        }

        // Анимация
        AnimationHelper.performFragmentCircularRevealAnimation(binding.root, 2)

        // Загружаем сохранённые фильмы
        viewModel.getSaved()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}