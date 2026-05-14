package com.arogyasahaya.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.arogyasahaya.ui.onboarding.OnboardingActivity
import com.arogyasahaya.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * First screen the user sees.
 * - Shows the Android 12+ Splash Screen.
 * - Checks if the user has completed onboarding.
 * - Routes to OnboardingActivity (first time) or MainActivity (returning user).
 */
@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val profileViewModel: ProfileViewModel by viewModels()
    private var isReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        // Keep the splash screen visible until we know where to navigate
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !isReady }
        super.onCreate(savedInstanceState)

        profileViewModel.checkOnboarding { isOnboardingDone ->
            isReady = true
            val destination = if (isOnboardingDone) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, OnboardingActivity::class.java)
            }
            startActivity(destination)
            finish()
        }
    }
}
