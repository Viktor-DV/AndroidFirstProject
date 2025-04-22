package com.dolgantsev.androindfirstproject

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.dolgantsev.androindfirstproject.data.dto.PreferenceProvider.Companion.CATEGORY_KEY
import com.dolgantsev.androindfirstproject.databinding.ActivityMainBinding
import com.dolgantsev.androindfirstproject.domain.Film
import com.dolgantsev.androindfirstproject.view.fragments.CollectionsFragment
import com.dolgantsev.androindfirstproject.view.fragments.DetailsFragment
import com.dolgantsev.androindfirstproject.view.fragments.FavoritesFragment
import com.dolgantsev.androindfirstproject.view.fragments.HomeFragment
import com.dolgantsev.androindfirstproject.view.fragments.SavedFragment
import com.dolgantsev.androindfirstproject.view.fragments.SettingsFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var startScreenAnimationView: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startScreenAnimationView = binding.startScreenAnimationView

        startScreenAnimationView.setRepeatCount(2)
        startScreenAnimationView.repeatMode = LottieDrawable.RESTART
        startScreenAnimationView.playAnimation()

        // Используем корутины вместо Handler
        lifecycleScope.launch {
            delay(3000)
            startScreenAnimationView.visibility = View.GONE

            if (savedInstanceState == null) {
                changeFragment(HomeFragment(), "home")
            }
        }

        val bottomNavigation = binding.bottomNavigation
        bottomNavigation.setOnItemSelectedListener { item ->
            val currentFragment = supportFragmentManager.fragments.lastOrNull()
            when (item.itemId) {
                R.id.home -> {
                    if (currentFragment !is HomeFragment) {
                        val tag = "home"
                        val fragment: Fragment = checkFragmentExistence(tag) ?: HomeFragment()
                        changeFragment(fragment, tag)
                    }
                    true
                }
                R.id.favorites -> {
                    if (currentFragment !is FavoritesFragment) {
                        val tag = "favorites"
                        val fragment: Fragment = checkFragmentExistence(tag) ?: FavoritesFragment()
                        changeFragment(fragment, tag)
                    }
                    true
                }
                R.id.watch_later -> {
                    if (currentFragment !is SavedFragment) {
                        val tag = "watch_later"
                        val fragment: Fragment = checkFragmentExistence(tag) ?: SavedFragment()
                        changeFragment(fragment, tag)
                    }
                    true
                }
                R.id.selections -> {
                    if (currentFragment !is CollectionsFragment) {
                        val tag = "selections"
                        val fragment: Fragment = checkFragmentExistence(tag) ?: CollectionsFragment()
                        changeFragment(fragment, tag)
                    }
                    true
                }
                R.id.settings -> {
                    if (currentFragment !is SettingsFragment) {
                        val tag = "settings"
                        val fragment: Fragment = checkFragmentExistence(tag) ?: SettingsFragment()
                        changeFragment(fragment, tag)
                    }
                    true
                }
                else -> false
            }
        }

        // Обработка нажатия "назад"
        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 1) {
                    supportFragmentManager.popBackStack() // Возвращаемся к предыдущему фрагменту
                } else {
                    AlertDialog.Builder(this@MainActivity)
                        .setMessage("Вы уверены, что хотите покинуть приложение?")
                        .setCancelable(false)
                        .setPositiveButton("Да") { _, _ ->
                            finish() // Завершаем активность
                        }
                        .setNegativeButton("Нет") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
            }
        })

        App.instance.preferences.registerListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        App.instance.preferences.unregisterListener(this)
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

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == CATEGORY_KEY) {
            val homeFragment = supportFragmentManager.findFragmentByTag("home") as? HomeFragment
            homeFragment?.updateMoviesList()
        }
    }
}