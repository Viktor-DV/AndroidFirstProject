package com.dolgantsev.androindfirstproject

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottom_navigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottom_navigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.favorites -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_placeholder, FavoritesFragment())
                        .addToBackStack(null)
                        .commit()
                    true
                }
                R.id.watch_later -> {
                    Toast.makeText(this, "Посмотреть позже", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.selections -> {
                    Toast.makeText(this, "Подборки", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_placeholder, HomeFragment())
            .addToBackStack(null)
            .commit()

        // Добавление обратного вызова для обработки нажатия кнопки "Назад"
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    AlertDialog.Builder(this@MainActivity)
                        .setMessage("Вы уверены, что хотите покинуть приложение?")
                        .setCancelable(false)
                        .setPositiveButton("Да") { dialog, id ->
                            isEnabled = false // Отключаем обратный вызов
                            onBackPressedDispatcher.onBackPressed()
                        }
                        .setNegativeButton("Нет") { dialog, id ->
                            dialog.dismiss()
                        }
                        .show()
                } else {
                    isEnabled = false // Отключаем обратный вызов
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    fun launchDetailsFragment(film: Film) {
        val bundle = Bundle()
        bundle.putParcelable("film", film)
        val fragment = DetailsFragment()
        fragment.arguments = bundle

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_placeholder, fragment)
            .addToBackStack(null)
            .commit()
    }
}
