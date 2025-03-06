package com.dolgantsev.androindfirstproject.data

import com.dolgantsev.androindfirstproject.R
import com.dolgantsev.androindfirstproject.domain.Film

class MainRepository {
    val filmsDataBase = listOf(
        Film("Побег из Шоушенка",
            R.drawable.film_poster1, "История дружбы двух заключенных, которые находят свободу даже за решеткой.", 7.7f),
        Film("Крестный отец",
            R.drawable.film_poster2, "Патриарх мафиозного клана передает власть своему сыну.", 7.7f),
        Film("Темный рыцарь",
            R.drawable.film_poster3, "Бэтмен противостоит Джокеру, который угрожает разрушить Готэм.", 7.7f),
        Film("Криминальное чтиво",
            R.drawable.film_poster4, "Несколько историй о криминальных персонажах, переплетающихся самым неожиданным образом.", 7.7f),
        Film("Список Шиндлера",
            R.drawable.film_poster5, "Немецкий промышленник спасает сотни евреев во время Холокоста.", 7.7f),
        Film("Форрест Гамп",
            R.drawable.film_poster6, "Простодушный парень с богатой историей, проживающий невероятную жизнь.", 7.7f),
        Film("Начало",
            R.drawable.film_poster7, "Команда профессионалов проникает в сны, чтобы внедрить идею.", 7.7f),
        Film("Матрица",
            R.drawable.film_poster8, "Хакер узнает, что реальный мир — это иллюзия, созданная машинами.", 7.7f),
        Film("Бойцовский клуб",
            R.drawable.film_poster9, "Два мужчины создают подпольный клуб, где правила диктует сила.", 7.7f),
        Film("Титаник",
            R.drawable.film_poster10, "Трагическая история любви на фоне крушения роскошного лайнера.", 7.7f)

    )
}