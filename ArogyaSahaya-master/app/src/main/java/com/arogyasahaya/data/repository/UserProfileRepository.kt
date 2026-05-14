package com.arogyasahaya.data.repository

import com.arogyasahaya.data.dao.UserProfileDao
import com.arogyasahaya.data.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepository @Inject constructor(
    private val profileDao: UserProfileDao
) {

    fun getUserProfile(): Flow<UserProfileEntity?> =
        profileDao.getUserProfile()

    suspend fun getUserProfileOnce(): UserProfileEntity? =
        profileDao.getUserProfileOnce()

    suspend fun saveProfile(profile: UserProfileEntity) =
        profileDao.insertOrUpdateProfile(profile)

    suspend fun markOnboardingComplete() =
        profileDao.markOnboardingComplete()

    suspend fun isOnboardingComplete(): Boolean =
        profileDao.isOnboardingComplete() ?: false
}
