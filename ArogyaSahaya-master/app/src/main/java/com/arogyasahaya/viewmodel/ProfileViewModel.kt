package com.arogyasahaya.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.arogyasahaya.data.entity.UserProfileEntity
import com.arogyasahaya.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: UserProfileRepository
) : ViewModel() {

    // Observe profile changes in real time
    val userProfile = profileRepository.getUserProfile().asLiveData()

    /**
     * Save or update the user's medical profile.
     */
    fun saveProfile(
        name: String,
        age: Int,
        gender: String,
        chronicConditions: String,
        emergencyContactName: String,
        emergencyContactPhone: String,
        bloodGroup: String
    ) {
        viewModelScope.launch {
            profileRepository.saveProfile(
                UserProfileEntity(
                    id                   = 1,
                    name                 = name,
                    age                  = age,
                    gender               = gender,
                    chronicConditions    = chronicConditions,
                    emergencyContactName = emergencyContactName,
                    emergencyContactPhone = emergencyContactPhone,
                    bloodGroup           = bloodGroup,
                    isOnboardingComplete = true
                )
            )
        }
    }

    /**
     * Check if onboarding was completed (used by SplashActivity).
     */
    fun checkOnboarding(callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            callback(profileRepository.isOnboardingComplete())
        }
    }
}
