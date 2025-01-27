package com.dolgantsev.androindfirstproject

import FilmListRecyclerAdapter
import TopSpacingItemDecoration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment() {

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
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Настройка RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.main_recycler)

        filmsAdapter = FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener{
            override fun click(film: Film) {
                (requireActivity() as MainActivity).launchDetailsFragment(film)
            }
        })

        recyclerView.adapter = filmsAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Добавляем разделители между элементами
        val decorator = TopSpacingItemDecoration(8)
        recyclerView.addItemDecoration(decorator)

        // Добавляем данные в адаптер
        filmsAdapter.addItems(filmsDataBase)
    }

}