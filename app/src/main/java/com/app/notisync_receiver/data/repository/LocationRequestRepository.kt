// ============================================================
// FILE: app/src/main/java/com/app/notisync_receiver/data/repository/LocationRequestRepository.kt
// Purpose: Sends location requests and reads location data
// ============================================================

package com.app.notisync_receiver.data.repository

import android.util.Log
import com.app.notisync_receiver.data.remote.FirestoreDataSource
import com.app.notisync_receiver.domain.model.DeviceLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRequestRepository @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource,
    private val authRepository: AuthRepository
) {

    companion object {
        private const val TAG = "LocationRequestRepo"
    }

    suspend fun requestLocation(deviceId: String): Result<Unit> {
        val userId = authRepository.getCurrentUserId()
        if (userId == null) {
            return Result.failure(Exception("User not logged in"))
        }

        return firestoreDataSource.sendLocationRequest(
            userId = userId,
            deviceId = deviceId,
            requestedByDeviceId = "receiver"
        )
    }

    fun observeDeviceLocations(deviceId: String): Flow<List<DeviceLocation>> {
        val userId = authRepository.getCurrentUserId() ?: return emptyFlow()
        return firestoreDataSource.observeDeviceLocations(userId, deviceId)
    }

    suspend fun getLatestLocation(deviceId: String): Result<DeviceLocation?> {
        val userId = authRepository.getCurrentUserId()
            ?: return Result.failure(Exception("User not logged in"))
        return firestoreDataSource.getLatestLocation(userId, deviceId)
    }

    suspend fun deleteLocation(deviceId: String, locationId: String): Result<Unit> {
        val userId = authRepository.getCurrentUserId()
            ?: return Result.failure(Exception("User not logged in"))
        return firestoreDataSource.deleteLocation(userId, deviceId, locationId)
    }
}
