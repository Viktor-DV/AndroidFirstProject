package com.dolgantsev.androindfirstproject

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment

class DetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val film: Film? = arguments?.getParcelable("film", Film::class.java)

        if (film != null) {
            val titleView: TextView = view.findViewById(R.id.title)
            val posterView: ImageView = view.findViewById(R.id.poster)
            val descriptionView: TextView = view.findViewById(R.id.description)

            titleView.text = film.title
            posterView.setImageResource(film.poster)
            descriptionView.text = film.description
        } else {
            val titleView: TextView = view.findViewById(R.id.title)
            titleView.text = getString(R.string.error_no_film)
        }
    }
}