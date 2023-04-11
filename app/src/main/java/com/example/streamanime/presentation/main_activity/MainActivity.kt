package com.example.streamanime.presentation.main_activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.streamanime.R
import com.example.streamanime.core.alarm.ExactAlarm
import com.example.streamanime.databinding.ActivityMainBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostMain) as NavHostFragment
        navController = navHostFragment.navController

        makeStatusBarTextVisible(true)

        binding.apply {
            viewModel.apply {
                btmNavView.setOnItemReselectedListener {}

                btmNavView.setupWithNavController(navController)

                errorMessage.observe(this@MainActivity) {
                    if (it.isNotBlank()) {
                        Snackbar.make(root, it, Snackbar.LENGTH_SHORT).show()
                    }
                }

                bookmarkedAnimes.observe(this@MainActivity) {
                    val animeWithUpdates = it.count { it.haveNewUpdate }
                    if (animeWithUpdates == 0) {
                        btmNavView.removeBadge(R.id.bookmarkedAnimeFragment)
                        return@observe
                    }
                    btmNavView.getOrCreateBadge(R.id.bookmarkedAnimeFragment).number = animeWithUpdates
                }
            }
        }
    }

    private fun makeStatusBarTextVisible(isLightUp: Boolean) {
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = isLightUp
    }
}