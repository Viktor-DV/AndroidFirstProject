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
import com.dolgantsev.androindfirstproject.utils.AnimationHelper
import com.dolgantsev.androindfirstproject.utils.ReminderManager
import com.dolgantsev.androindfirstproject.view.rv_adapters.FilmListRecyclerAdapter
import com.dolgantsev.androindfirstproject.view.rv_adapters.FilmListRecyclerAdapter.OnReminderActionListener
import com.dolgantsev.androindfirstproject.view.rv_adapters.TopSpacingItemDecoration
import com.dolgantsev.androindfirstproject.viewmodel.SavedFragmentViewModel
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.Calendar
import android.app.TimePickerDialog
import androidx.appcompat.app.AlertDialog
import com.dolgantsev.androindfirstproject.utils.Reminder

class SavedFragment : Fragment() {

    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SavedFragmentViewModel by viewModels()
    private lateinit var filmsAdapter: FilmListRecyclerAdapter
    private val disposables = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        filmsAdapter = FilmListRecyclerAdapter(
            object : FilmListRecyclerAdapter.OnItemClickListener {
                override fun click(film: com.dolgantsev.androindfirstproject.domain.Film) {
                    (requireActivity() as MainActivity).launchDetailsFragment(film)
                }
            },
            object : OnReminderActionListener {
                override fun onEditReminder(film: com.dolgantsev.androindfirstproject.domain.Film) {
                    filmsAdapter.notifyDataSetChanged()
                }

                override fun onDeleteReminder(film: com.dolgantsev.androindfirstproject.domain.Film) {
                    filmsAdapter.notifyDataSetChanged()
                }

                override fun onCreateReminder(film: com.dolgantsev.androindfirstproject.domain.Film) {
                    showTimePickerDialog(film)
                }
            }
        )

        binding.savedRecycler.apply {
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(TopSpacingItemDecoration(8))
        }

        viewModel.savedFilms.observe(viewLifecycleOwner) { films ->
            filmsAdapter.submitList(films)
        }

        AnimationHelper.performFragmentCircularRevealAnimation(binding.root, 2)

        viewModel.getSaved()

        binding.btnDeleteAll.setOnClickListener {
            showDeleteAllDialog()
        }

        binding.btnDeleteAllReminders.setOnClickListener {
            ReminderManager.deleteAllReminders(requireContext())
            filmsAdapter.notifyDataSetChanged()
            Snackbar.make(binding.root, "Все уведомления удалены", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun showTimePickerDialog(film: com.dolgantsev.androindfirstproject.domain.Film) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            val newTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, selectedHour)
                set(Calendar.MINUTE, selectedMinute)
                set(Calendar.SECOND, 0)
                if (before(Calendar.getInstance())) {
                    add(Calendar.DATE, 1)
                }
            }.timeInMillis

            AlertDialog.Builder(requireContext())
                .setTitle("Подтверждение")
                .setMessage("Сохранить напоминание на ${selectedHour}:${selectedMinute}?")
                .setPositiveButton("Да") { _, _ ->
                    ReminderManager.saveReminder(requireContext(), Reminder(film.id, newTime))
                    filmsAdapter.notifyDataSetChanged()
                }
                .setNegativeButton("Нет", null)
                .show()
        }, hour, minute, true).show()
    }

    private fun showDeleteAllDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Подтверждение")
            .setMessage("Удалить все сохраненные фильмы и уведомления?")
            .setPositiveButton("Да") { _, _ ->
                disposables.add(
                    viewModel.clearAllFilms()
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            {
                                ReminderManager.deleteAllReminders(requireContext())
                                filmsAdapter.submitList(emptyList())
                                Snackbar.make(binding.root, "Все удалено", Snackbar.LENGTH_SHORT).show()
                            },
                            { error ->
                                Snackbar.make(binding.root, "Ошибка: ${error.message}", Snackbar.LENGTH_SHORT).show()
                            }
                        )
                )
            }
            .setNegativeButton("Нет", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
        _binding = null
    }
}