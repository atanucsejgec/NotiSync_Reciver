// ============================================================
// FILE: app/src/main/java/com/app/notisync_receiver/data/repository/FeatureFlagRepository.kt
// Purpose: Controls feature flags for sender devices
// ============================================================

package com.app.notisync_receiver.data.repository

import com.app.notisync_receiver.data.remote.FirestoreDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeatureFlagRepository @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource,
    private val authRepository: AuthRepository
) {

    suspend fun setKeyboardCaptureEnabled(deviceId: String, enabled: Boolean): Result<Unit> {
        return updateFlag(deviceId, "keyboardCaptureEnabled", enabled)
    }

    suspend fun setNotificationSyncEnabled(deviceId: String, enabled: Boolean): Result<Unit> {
        return updateFlag(deviceId, "notificationSyncEnabled", enabled)
    }

    suspend fun setCallLogSyncEnabled(deviceId: String, enabled: Boolean): Result<Unit> {
        return updateFlag(deviceId, "callLogSyncEnabled", enabled)
    }

    suspend fun setLocationTrackingEnabled(deviceId: String, enabled: Boolean): Result<Unit> {
        return updateFlag(deviceId, "locationTrackingEnabled", enabled)
    }

    private suspend fun updateFlag(deviceId: String, flagName: String, value: Boolean): Result<Unit> {
        val userId = authRepository.getCurrentUserId()
            ?: return Result.failure(Exception("User not logged in"))

        return firestoreDataSource.updateFeatureFlag(
            userId = userId,
            deviceId = deviceId,
            flagName = flagName,
            flagValue = value
        )
    }

    fun observeFeatureFlags(deviceId: String): Flow<Map<String, Boolean>> {
        val userId = authRepository.getCurrentUserId() ?: return emptyFlow()
        return firestoreDataSource.observeFeatureFlags(userId, deviceId)
    }
}
