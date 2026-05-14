package com.arogyasahaya.data.dao

import androidx.room.*
import com.arogyasahaya.data.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {

    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileOnce(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    @Query("UPDATE user_profile SET isOnboardingComplete = 1 WHERE id = 1")
    suspend fun markOnboardingComplete()

    @Query("SELECT isOnboardingComplete FROM user_profile WHERE id = 1")
    suspend fun isOnboardingComplete(): Boolean?
}
