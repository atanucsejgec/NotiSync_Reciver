// ============================================================
// FILE: app/src/main/java/com/app/notisync_receiver/data/repository/DeviceRepository.kt
// Purpose: Manages sender device data from Firestore
// ============================================================

package com.app.notisync_receiver.data.repository

import android.util.Log
import com.app.notisync_receiver.data.remote.FirestoreDataSource
import com.app.notisync_receiver.domain.model.SenderDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceRepository @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource,
    private val authRepository: AuthRepository
) {

    companion object {
        private const val TAG = "DeviceRepository"
    }

    fun observeDevices(): Flow<List<SenderDevice>> {
        val userId = authRepository.getCurrentUserId()
        if (userId == null) {
            Log.w(TAG, "No user logged in — cannot observe devices")
            return emptyFlow()
        }

        return firestoreDataSource.observeDevices(userId)
    }

    suspend fun getDevices(): Result<List<SenderDevice>> {
        val userId = authRepository.getCurrentUserId()
        if (userId == null) {
            Log.w(TAG, "No user logged in — cannot get devices")
            return Result.failure(Exception("User not logged in"))
        }

        return firestoreDataSource.getDevices(userId)
    }

    suspend fun deleteDevice(deviceId: String): Result<Unit> {
        val userId = authRepository.getCurrentUserId()
        if (userId == null) {
            return Result.failure(Exception("User not logged in"))
        }
        Log.d(TAG, "Deleting device: $deviceId")
        return firestoreDataSource.deleteDevice(userId, deviceId)
    }
}
