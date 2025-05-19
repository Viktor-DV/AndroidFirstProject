package com.dolgantsev.androindfirstproject.view.rv_viewholders

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dolgantsev.androindfirstproject.R
import com.dolgantsev.androindfirstproject.domain.Film
import com.dolgantsev.androindfirstproject.utils.ReminderManager
import com.dolgantsev.androindfirstproject.view.customviews.RatingDonutView
import com.dolgantsev.androindfirstproject.view.rv_adapters.FilmListRecyclerAdapter
import java.text.SimpleDateFormat
import java.util.*

class FilmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val title: TextView = itemView.findViewById(R.id.title)
    private val poster: ImageView = itemView.findViewById(R.id.poster)
    private val overview: TextView = itemView.findViewById(R.id.overview)
    private val ratingDonut: RatingDonutView = itemView.findViewById(R.id.rating_donut)
    private val reminderTime: TextView = itemView.findViewById(R.id.reminder_time)
    private val btnEditReminder: ImageButton = itemView.findViewById(R.id.btn_edit_reminder)
    private val btnDeleteReminder: Button = itemView.findViewById(R.id.btn_delete_reminder)

    fun bind(film: Film, listener: FilmListRecyclerAdapter.OnReminderActionListener?) {
        title.text = film.title
        overview.text = film.overview
        Glide.with(itemView)
            .load(com.dolgantsev.androindfirstproject.network.api.ApiConstants.IMAGES_URL + "w342" + film.posterPath)
            .centerCrop()
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_error)
            .into(poster)

        ratingDonut.setProgress(0)
        ratingDonut.setProgress((film.rating * 10).toInt(), animate = true)

        val context = itemView.context
        val hasReminder = ReminderManager.hasReminder(context, film.id)
        if (hasReminder) {
            val reminderTimeText = ReminderManager.getReminderTime(context, film.id)?.let {
                SimpleDateFormat("dd MMMM, HH:mm", Locale.getDefault()).format(Date(it))
            } ?: "Не задано"
            reminderTime.text = context.getString(R.string.reminder_time_placeholder, reminderTimeText)
            reminderTime.visibility = View.VISIBLE
        } else {
            reminderTime.visibility = View.GONE
        }
        btnDeleteReminder.visibility = if (hasReminder) View.VISIBLE else View.GONE

        btnEditReminder.setOnClickListener {
            showDatePickerDialog(context, film, listener)
        }
        btnDeleteReminder.setOnClickListener {
            ReminderManager.deleteReminder(context, film.id)
            listener?.onDeleteReminder(film)
        }
    }

    private fun showDatePickerDialog(context: Context, film: Film, listener: FilmListRecyclerAdapter.OnReminderActionListener?) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedCalendar = Calendar.getInstance().apply {
                set(selectedYear, selectedMonth, selectedDay)
            }
            showTimePickerDialog(context, selectedCalendar, selectedDay, selectedMonth, selectedYear, film, listener)
        }, year, month, day).show()
    }

    private fun showTimePickerDialog(
        context: Context,
        selectedCalendar: Calendar,
        selectedDay: Int,
        selectedMonth: Int,
        selectedYear: Int,
        film: Film,
        listener: FilmListRecyclerAdapter.OnReminderActionListener?
    ) {
        val hour = selectedCalendar.get(Calendar.HOUR_OF_DAY)
        val minute = selectedCalendar.get(Calendar.MINUTE)

        TimePickerDialog(context, { _, selectedHour, selectedMinute ->
            val newTime = selectedCalendar.apply {
                set(Calendar.HOUR_OF_DAY, selectedHour)
                set(Calendar.MINUTE, selectedMinute)
                set(Calendar.SECOND, 0)
                if (before(Calendar.getInstance())) {
                    add(Calendar.DATE, 1)
                }
            }.timeInMillis

            AlertDialog.Builder(context)
                .setTitle("Подтверждение")
                .setMessage("Сохранить напоминание на ${selectedDay}.${selectedMonth + 1}.${selectedYear} ${selectedHour}:${selectedMinute}?")
                .setPositiveButton("Да") { _, _ ->
                    ReminderManager.updateReminder(context, film.id, newTime)
                    listener?.onEditReminder(film)
                }
                .setNegativeButton("Нет", null)
                .show()
        }, hour, minute, true).show()
    }
}