package com.dolgantsev.androindfirstproject.view.rv_adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dolgantsev.androindfirstproject.R

class CollectionsAdapter(
    private val clickListener: FilmListRecyclerAdapter.OnItemClickListener
) : ListAdapter<Pair<String, List<com.dolgantsev.androindfirstproject.domain.Film>>, CollectionsAdapter.CollectionViewHolder>(CollectionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.collection_item, parent, false)
        return CollectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        val (category, films) = getItem(position)
        holder.bind(category, films)
    }

    fun submitCollections(newCollections: Map<String, List<com.dolgantsev.androindfirstproject.domain.Film>>) {
        submitList(newCollections.toList())
    }

    inner class CollectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryTitle: TextView = itemView.findViewById(R.id.category_title)
        private val filmsRecycler: RecyclerView = itemView.findViewById(R.id.films_recycler)

        fun bind(category: String, films: List<com.dolgantsev.androindfirstproject.domain.Film>) {
            categoryTitle.text = when (category) {
                "popular" -> "Популярные"
                "top_rated" -> "Высокий рейтинг"
                "upcoming" -> "Скоро в прокате"
                "now_playing" -> "Сейчас в прокате"
                else -> category
            }

            val filmsAdapter = FilmListRecyclerAdapter(clickListener)
            filmsRecycler.apply {
                adapter = filmsAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }
            filmsAdapter.submitList(films)
        }
    }
}

class CollectionDiffCallback : DiffUtil.ItemCallback<Pair<String, List<com.dolgantsev.androindfirstproject.domain.Film>>>() {
    override fun areItemsTheSame(oldItem: Pair<String, List<com.dolgantsev.androindfirstproject.domain.Film>>, newItem: Pair<String, List<com.dolgantsev.androindfirstproject.domain.Film>>): Boolean {
        return oldItem.first == newItem.first
    }

    override fun areContentsTheSame(oldItem: Pair<String, List<com.dolgantsev.androindfirstproject.domain.Film>>, newItem: Pair<String, List<com.dolgantsev.androindfirstproject.domain.Film>>): Boolean {
        return oldItem == newItem
    }
}