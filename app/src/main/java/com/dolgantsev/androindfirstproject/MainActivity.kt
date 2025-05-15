package com.dolgantsev.androindfirstproject

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.dolgantsev.androindfirstproject.databinding.ActivityMainBinding
import com.dolgantsev.androindfirstproject.utils.BatteryReceiver
import com.dolgantsev.androindfirstproject.view.fragments.*
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var startScreenAnimationView: LottieAnimationView
    private val disposables = CompositeDisposable()
    private val batteryReceiver = BatteryReceiver()

    // Лаунчер для запроса разрешения WRITE_SETTINGS
    private val requestWriteSettings = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (!Settings.System.canWrite(this)) {
            // Разрешение не получено, показываем Snackbar
            showPermissionDeniedSnackbar()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Анимация заставки
        startScreenAnimationView = binding.startScreenAnimationView
        startScreenAnimationView.setRepeatCount(2)
        startScreenAnimationView.repeatMode = LottieDrawable.RESTART
        startScreenAnimationView.playAnimation()

        // Используем Handler для задержки
        Handler(Looper.getMainLooper()).postDelayed({
            startScreenAnimationView.visibility = View.GONE
            if (savedInstanceState == null) {
                changeFragment(HomeFragment(), "home")
            }
        }, 3000)

        // Обработка навигации по нижнему меню
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val currentFragment = supportFragmentManager.fragments.lastOrNull()
            when (item.itemId) {
                R.id.home -> {
                    if (currentFragment !is HomeFragment) {
                        val tag = "home"
                        val fragment = checkFragmentExistence(tag) ?: HomeFragment()
                        changeFragment(fragment, tag)
                    }
                    true
                }
                R.id.favorites -> {
                    if (currentFragment !is FavoritesFragment) {
                        val tag = "favorites"
                        val fragment = checkFragmentExistence(tag) ?: FavoritesFragment()
                        changeFragment(fragment, tag)
                    }
                    true
                }
                R.id.watch_later -> {
                    if (currentFragment !is SavedFragment) {
                        val tag = "watch_later"
                        val fragment = checkFragmentExistence(tag) ?: SavedFragment()
                        changeFragment(fragment, tag)
                    }
                    true
                }
                R.id.selections -> {
                    if (currentFragment !is CollectionsFragment) {
                        val tag = "selections"
                        val fragment = checkFragmentExistence(tag) ?: CollectionsFragment()
                        changeFragment(fragment, tag)
                    }
                    true
                }
                R.id.settings -> {
                    if (currentFragment !is SettingsFragment) {
                        val tag = "settings"
                        val fragment = checkFragmentExistence(tag) ?: SettingsFragment()
                        changeFragment(fragment, tag)
                    }
                    true
                }
                else -> false
            }
        }

        // Обработка кнопки "Назад"
        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 1) {
                    supportFragmentManager.popBackStack()
                } else {
                    AlertDialog.Builder(this@MainActivity)
                        .setMessage("Вы уверены, что хотите покинуть приложение?")
                        .setCancelable(false)
                        .setPositiveButton("Да") { _, _ -> finish() }
                        .setNegativeButton("Нет") { dialog, _ -> dialog.dismiss() }
                        .show()
                }
            }
        })

        // Подписка на изменения предпочтений
        val prefsDisposable: Disposable = App.instance.preferences
            .asObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { _: Any? ->
                    val homeFragment = supportFragmentManager.findFragmentByTag("home") as? HomeFragment
                    homeFragment?.updateMoviesList()
                },
                { error: Throwable ->
                    error.printStackTrace()
                }
            )
        disposables.add(prefsDisposable)

        // Регистрируем ресивер на события батареи
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_LOW)
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_BATTERY_CHANGED)
        }
        registerReceiver(batteryReceiver, filter)

        // Запрашиваем разрешение на изменение настроек
        requestWriteSettingsPermission()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(batteryReceiver)
        disposables.clear()
    }

    // Переключение фрагментов
    @SuppressLint("CommitTransaction")
    private fun changeFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_placeholder, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }

    // Проверка наличия фрагмента в стеке
    private fun checkFragmentExistence(tag: String): Fragment? {
        return supportFragmentManager.findFragmentByTag(tag)
    }

    // Запуск экрана деталей фильма
    fun launchDetailsFragment(film: com.dolgantsev.androindfirstproject.domain.Film) {
        val bundle = Bundle().apply {
            putParcelable("film", film)
        }
        val fragment = DetailsFragment().apply {
            arguments = bundle
        }
        changeFragment(fragment, "details")
    }

    // Метод для запроса разрешения WRITE_SETTINGS
    private fun requestWriteSettingsPermission() {
        if (!Settings.System.canWrite(this)) {
            Snackbar.make(
                binding.root,
                "Для управления яркостью необходимо разрешение. Разрешить сейчас?",
                Snackbar.LENGTH_LONG
            )
                .setAction("Да") {
                    requestWriteSettings.launch(Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS))
                }
                .show()
        }
    }

    // Метод для показа Snackbar при отказе от разрешения
    private fun showPermissionDeniedSnackbar() {
        Snackbar.make(
            binding.root,
            "Для управления яркостью необходимо разрешение. Попробовать снова?",
            Snackbar.LENGTH_LONG
        )
            .setAction("Да") {
                requestWriteSettings.launch(Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS))
            }
            .show()
    }
}