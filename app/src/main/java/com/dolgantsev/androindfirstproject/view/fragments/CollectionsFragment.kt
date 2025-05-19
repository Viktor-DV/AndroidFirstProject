package com.dolgantsev.androindfirstproject.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dolgantsev.androindfirstproject.MainActivity
import com.dolgantsev.androindfirstproject.databinding.FragmentCollectionsBinding
import com.dolgantsev.androindfirstproject.utils.AnimationHelper
import com.dolgantsev.androindfirstproject.view.rv_adapters.CollectionsAdapter
import com.dolgantsev.androindfirstproject.view.rv_adapters.FilmListRecyclerAdapter
import com.dolgantsev.androindfirstproject.viewmodel.CollectionsFragmentViewModel
import com.google.android.material.snackbar.Snackbar

class CollectionsFragment : Fragment() {

    private var _binding: FragmentCollectionsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CollectionsFragmentViewModel by viewModels()
    private lateinit var collectionsAdapter: CollectionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectionsAdapter = CollectionsAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
            override fun click(film: com.dolgantsev.androindfirstproject.domain.Film) {
                (requireActivity() as MainActivity).launchDetailsFragment(film)
            }
        })

        binding.collectionsRecycler.apply {
            adapter = collectionsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // Наблюдаем за коллекциями через LiveData
        viewModel.collections.observe(viewLifecycleOwner) { collections ->
            collectionsAdapter.submitCollections(collections)
        }

        // Наблюдаем за errorEvent
        viewModel.errorEvent.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }

        // Проверяем анимацию
        AnimationHelper.performFragmentCircularRevealAnimation(binding.collectionsRecycler, 4)

        // Добавляем вызов loadCollections
        viewModel.loadCollections()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}