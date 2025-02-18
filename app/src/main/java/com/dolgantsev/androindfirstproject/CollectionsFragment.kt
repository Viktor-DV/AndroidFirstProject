package com.dolgantsev.androindfirstproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dolgantsev.androindfirstproject.databinding.FragmentCollectionsBinding

class CollectionsFragment : Fragment() {

    private var _binding: FragmentCollectionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCollectionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Анимация
        AnimationHelper.performFragmentCircularRevealAnimation(binding.root, requireActivity(), 4)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
