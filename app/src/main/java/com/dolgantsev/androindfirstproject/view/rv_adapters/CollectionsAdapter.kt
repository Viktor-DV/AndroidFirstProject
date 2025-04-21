package com.dolgantsev.androindfirstproject.view.rv_adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dolgantsev.androindfirstproject.R
import com.dolgantsev.androindfirstproject.domain.Film

class CollectionsAdapter(
    private val clickListener: FilmListRecyclerAdapter.OnItemClickListener
) : RecyclerView.Adapter<CollectionsAdapter.CollectionViewHolder>() {

    private var collections: List<Pair<String, List<Film>>> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.collection_item, parent, false)
        return CollectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        val (category, films) = collections[position]
        holder.bind(category, films)
    }

    override fun getItemCount(): Int = collections.size

    fun submitCollections(newCollections: Map<String, List<Film>>) {
        collections = newCollections.toList()
        notifyDataSetChanged()
    }

    inner class CollectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryTitle: TextView = itemView.findViewById(R.id.category_title)
        private val filmsRecycler: RecyclerView = itemView.findViewById(R.id.films_recycler)

        fun bind(category: String, films: List<Film>) {
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