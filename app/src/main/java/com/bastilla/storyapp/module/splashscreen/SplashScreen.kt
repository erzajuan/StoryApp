package com.bastilla.storyapp.module.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import com.bastilla.storyapp.R
import com.bastilla.storyapp.module.main.MainActivity
import com.bastilla.storyapp.module.main.settings.SettingsViewModel
import com.bastilla.storyapp.module.sign.SignActivity
import com.bastilla.storyapp.module.sign.SignViewModel
import com.bastilla.storyapp.module.sign.ViewModelFactory

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var factory: ViewModelFactory
    private val settingViewModel: SettingsViewModel by viewModels { factory }
    private val authenticationViewModel: SignViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        factory = ViewModelFactory.getInstance(this)

        val delayMillis: Long = 2000
        setupView()

        settingViewModel.getThemeMode().observe(this) { isNightMode ->
            if (isNightMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        settingViewModel.getIsFirstTime().observe(this) {
            authenticationViewModel.getUserToken().observe(this) { token ->
                if (token.isNullOrEmpty() || token == "not_set_yet") {
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent = Intent(this@SplashScreenActivity, SignActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, delayMillis)
                } else {
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, delayMillis)
                }
            }
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()

    }
}