package com.arogyasahaya.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.arogyasahaya.data.repository.MedicineRepository
import com.arogyasahaya.data.repository.UserProfileRepository
import com.arogyasahaya.data.repository.VitalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val medicineRepository: MedicineRepository,
    private val profileRepository: UserProfileRepository,
    private val vitalRepository: VitalRepository
) : ViewModel() {

    // User profile — observed by HomeFragment
    val userProfile = profileRepository.getUserProfile().asLiveData()

    // All active medicines
    val medicines = medicineRepository.getAllActiveMedicines().asLiveData()

    // Today's vital log (to show a summary on home screen)
    val todayVitals = vitalRepository.getVitalLogsSince(
        System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)
    ).asLiveData()

    // Recent dose logs for today
    val todayDoseLogs = run {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = cal.timeInMillis
        val endOfDay   = startOfDay + TimeUnit.DAYS.toMillis(1)
        medicineRepository.getLogsForDay(startOfDay, endOfDay).asLiveData()
    }

    // 7-day adherence rate (0-100%)
    fun loadAdherenceRate(callback: (Float) -> Unit) {
        viewModelScope.launch {
            val sevenDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)
            val rate = medicineRepository.getAdherenceRate(sevenDaysAgo)
            callback(rate)
        }
    }
}
