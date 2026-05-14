package com.arogyasahaya.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1,   // Single user — always id=1
    val name: String = "",
    val age: Int = 0,
    val gender: String = "",
    val chronicConditions: String = "",   // comma-separated
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
    val bloodGroup: String = "",
    val isOnboardingComplete: Boolean = false
)
