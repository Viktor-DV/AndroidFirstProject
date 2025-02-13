package com.dolgantsev.androindfirstproject

import FilmListRecyclerAdapter
import TopSpacingItemDecoration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dolgantsev.androindfirstproject.databinding.MergeHomeScreenContentBinding
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: MergeHomeScreenContentBinding? = null
    private val binding get() = _binding!!

    private val filmsDataBase = listOf(
        Film("Побег из Шоушенка", R.drawable.film_poster1, "История дружбы двух заключенных, которые находят свободу даже за решеткой."),
        Film("Крестный отец", R.drawable.film_poster2, "Патриарх мафиозного клана передает власть своему сыну."),
        Film("Темный рыцарь", R.drawable.film_poster3, "Бэтмен противостоит Джокеру, который угрожает разрушить Готэм."),
        Film("Криминальное чтиво", R.drawable.film_poster4, "Несколько историй о криминальных персонажах, переплетающихся самым неожиданным образом."),
        Film("Список Шиндлера", R.drawable.film_poster5, "Немецкий промышленник спасает сотни евреев во время Холокоста."),
        Film("Форрест Гамп", R.drawable.film_poster6, "Простодушный парень с богатой историей, проживающий невероятную жизнь."),
        Film("Начало", R.drawable.film_poster7, "Команда профессионалов проникает в сны, чтобы внедрить идею."),
        Film("Матрица", R.drawable.film_poster8, "Хакер узнает, что реальный мир — это иллюзия, созданная машинами."),
        Film("Бойцовский клуб", R.drawable.film_poster9, "Два мужчины создают подпольный клуб, где правила диктует сила."),
        Film("Титаник", R.drawable.film_poster10, "Трагическая история любви на фоне крушения роскошного лайнера.")
    )

    private lateinit var filmsAdapter: FilmListRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MergeHomeScreenContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Настройка RecyclerView
        filmsAdapter = FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
            override fun click(film: Film) {
                (requireActivity() as MainActivity).launchDetailsFragment(film)
            }
        })

        binding.mainRecycler.apply {
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(TopSpacingItemDecoration(8))
        }

        // Добавляем данные в адаптер
        filmsAdapter.addItems(filmsDataBase)

        // Приводим SearchView в развернутое состояние при клике
        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
        }

        // Устанавливаем слушатель для SearchView
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = if (newText.isNullOrEmpty()) {
                    filmsDataBase
                } else {
                    filmsDataBase.filter {
                        it.title.lowercase(Locale.getDefault()).contains(newText.lowercase(Locale.getDefault()))
                    }
                }
                filmsAdapter.addItems(filteredList)
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
