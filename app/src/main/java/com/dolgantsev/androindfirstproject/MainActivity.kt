package com.dolgantsev.androindfirstproject

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.dolgantsev.androindfirstproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var startScreenAnimationView: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startScreenAnimationView = binding.startScreenAnimationView

        // Настройка анимации Lottie
        startScreenAnimationView.setRepeatCount(2) // Повторить 2 раза (всего 3 проигрывания)
        startScreenAnimationView.repeatMode = LottieDrawable.RESTART // Использование правильной константы

        // Запуск анимации
        startScreenAnimationView.playAnimation()

        // Используем Handler для ожидания окончания анимации
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            // Скрываем анимацию и показываем фрагменты
            startScreenAnimationView.visibility = View.GONE

            // Инициализация с HomeFragment
            if (savedInstanceState == null) {
                changeFragment(HomeFragment(), "home")
            }
        }, 3000) // Задержка в 3 секунды

        val bottomNavigation = binding.bottomNavigation
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val tag = "home"
                    val fragment = checkFragmentExistence(tag) ?: HomeFragment()
                    changeFragment(fragment, tag)
                    true
                }
                R.id.favorites -> {
                    val tag = "favorites"
                    val fragment = checkFragmentExistence(tag) ?: FavoritesFragment()
                    changeFragment(fragment, tag)
                    true
                }
                R.id.watch_later -> {
                    val tag = "watch_later"
                    val fragment = checkFragmentExistence(tag) ?: SavedFragment()
                    changeFragment(fragment, tag)
                    true
                }
                R.id.selections -> {
                    val tag = "selections"
                    val fragment = checkFragmentExistence(tag) ?: CollectionsFragment()
                    changeFragment(fragment, tag)
                    true
                }
                else -> false
            }
        }

        // Обработка нажатия кнопки "Назад"
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    AlertDialog.Builder(this@MainActivity)
                        .setMessage("Вы уверены, что хотите покинуть приложение?")
                        .setCancelable(false)
                        .setPositiveButton("Да") { _, _ ->
                            isEnabled = false
                            onBackPressedDispatcher.onBackPressed()
                        }
                        .setNegativeButton("Нет") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun changeFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_placeholder, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }

    private fun checkFragmentExistence(tag: String): Fragment? {
        return supportFragmentManager.findFragmentByTag(tag)
    }

    fun launchDetailsFragment(film: Film) {
        val bundle = Bundle().apply {
            putParcelable("film", film)
        }
        val fragment = DetailsFragment().apply {
            arguments = bundle
        }
        changeFragment(fragment, "details")
    }
}
