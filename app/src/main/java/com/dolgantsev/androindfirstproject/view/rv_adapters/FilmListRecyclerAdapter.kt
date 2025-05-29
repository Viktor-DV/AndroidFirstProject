package com.dolgantsev.androindfirstproject.view.rv_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dolgantsev.androindfirstproject.R
import com.dolgantsev.androindfirstproject.domain.Film
import com.dolgantsev.androindfirstproject.utils.ReminderManager
import com.dolgantsev.androindfirstproject.view.rv_viewholders.FilmViewHolder

class FilmListRecyclerAdapter(
    private val clickListener: OnItemClickListener,
    private val reminderListener: OnReminderActionListener? = null
) : ListAdapter<Film, RecyclerView.ViewHolder>(FilmDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_film_with_reminder, parent, false)
        return FilmViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FilmViewHolder -> {
                val film = getItem(position)
                holder.bind(film, reminderListener)
                holder.itemView.findViewById<CardView>(R.id.item_container).setOnClickListener {
                    clickListener.click(film)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun click(film: Film)
    }

    interface OnReminderActionListener {
        fun onEditReminder(film: Film)
        fun onDeleteReminder(film: Film)
        fun onCreateReminder(film: Film)
    }
}

class FilmDiffCallback : DiffUtil.ItemCallback<Film>() {
    override fun areItemsTheSame(oldItem: Film, newItem: Film): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Film, newItem: Film): Boolean {
        return oldItem == newItem
    }
}