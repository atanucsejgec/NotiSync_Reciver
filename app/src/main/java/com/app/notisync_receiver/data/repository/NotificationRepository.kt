// ============================================================
// FILE: app/src/main/java/com/app/notisync_receiver/data/repository/NotificationRepository.kt
// Purpose: Manages notifications from Firestore and local Room database
// ============================================================

package com.app.notisync_receiver.data.repository

import android.util.Log
import com.app.notisync_receiver.data.local.ReceivedNotificationDao
import com.app.notisync_receiver.data.local.ReceivedNotificationEntity
import com.app.notisync_receiver.data.remote.FirestoreDataSource
import com.app.notisync_receiver.domain.model.NotificationBatch
import com.app.notisync_receiver.domain.model.ReceivedNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource,
    private val notificationDao: ReceivedNotificationDao,
    private val authRepository: AuthRepository
) {

    companion object {
        private const val TAG = "NotificationRepository"
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    fun observeNotificationsFromFirestore(deviceId: String): Flow<List<NotificationBatch>> {
        val userId = authRepository.getCurrentUserId()
        if (userId == null) {
            Log.w(TAG, "No user logged in")
            return emptyFlow()
        }

        return firestoreDataSource.observeNotifications(userId, deviceId)
    }

    fun observeAllNotificationsFromFirestore(): Flow<List<NotificationBatch>> {
        val userId = authRepository.getCurrentUserId()
        if (userId == null) {
            Log.w(TAG, "No user logged in")
            return emptyFlow()
        }

        return firestoreDataSource.observeAllNotifications(userId)
    }

    fun observeLocalNotifications(): Flow<List<ReceivedNotification>> {
        return notificationDao.observeAllNotifications().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    fun observeLocalNotificationsByDevice(deviceId: String): Flow<List<ReceivedNotification>> {
        return notificationDao.observeNotificationsByDevice(deviceId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    fun observeUnreadCount(): Flow<Int> {
        return notificationDao.observeUnreadCount()
    }

    fun observeUnreadCountByDevice(deviceId: String): Flow<Int> {
        return notificationDao.observeUnreadCountByDevice(deviceId)
    }

    suspend fun saveNotifications(batch: NotificationBatch) {
        val entities = batch.notifications.map { notification ->
            notification.toEntity()
        }

        val newEntities = entities.filter { entity ->
            !notificationDao.exists(entity.uniqueKey)
        }

        if (newEntities.isNotEmpty()) {
            notificationDao.insertNotifications(newEntities)
            Log.d(TAG, "Saved ${newEntities.size} new notifications from batch ${batch.batchId}")
        }
    }

    suspend fun saveNotificationsFromBatches(batches: List<NotificationBatch>) {
        batches.forEach { batch ->
            saveNotifications(batch)
        }
    }

    fun syncNotifications(deviceId: String) {
        val userId = authRepository.getCurrentUserId() ?: return

        scope.launch {
            try {
                val result = firestoreDataSource.getNotificationBatches(userId, deviceId)
                result.onSuccess { batches ->
                    saveNotificationsFromBatches(batches)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Sync failed: ${e.message}", e)
            }
        }
    }

    suspend fun markAsRead(notificationId: String) {
        notificationDao.markAsRead(notificationId)
    }

    suspend fun markAllAsRead() {
        notificationDao.markAllAsRead()
    }

    suspend fun markAllAsReadByDevice(deviceId: String) {
        notificationDao.markAllAsReadByDevice(deviceId)
    }

    suspend fun deleteNotification(notificationId: String) {
        notificationDao.deleteNotification(notificationId)
    }

    suspend fun deleteNotificationFromFirestore(deviceId: String, batchId: String) {
        val userId = authRepository.getCurrentUserId() ?: return
        firestoreDataSource.deleteNotification(userId, deviceId, batchId)
        // Also delete from local Room DB
        notificationDao.deleteNotification(batchId)
    }

    suspend fun deleteNotificationsByDevice(deviceId: String) {
        notificationDao.deleteNotificationsByDevice(deviceId)
    }

    suspend fun deleteAllNotificationsFromFirestore(deviceId: String) {
        val userId = authRepository.getCurrentUserId() ?: return
        firestoreDataSource.deleteAllNotifications(userId, deviceId)
        // Also delete from local Room DB
        notificationDao.deleteNotificationsByDevice(deviceId)
    }

    suspend fun clearAllNotifications() {
        notificationDao.deleteAllNotifications()
    }

    suspend fun cleanupOldNotifications(daysToKeep: Int = 7) {
        val cutoffTimestamp = System.currentTimeMillis() - (daysToKeep * 24 * 60 * 60 * 1000L)
        notificationDao.deleteOldNotifications(cutoffTimestamp)
    }

    private fun ReceivedNotificationEntity.toDomainModel(): ReceivedNotification {
        return ReceivedNotification(
            id = id,
            appName = appName,
            title = title,
            message = message,
            timestamp = timestamp,
            deviceId = deviceId,
            deviceName = deviceName,
            batchId = batchId,
            uniqueKey = uniqueKey
        )
    }

    private fun ReceivedNotification.toEntity(): ReceivedNotificationEntity {
        return ReceivedNotificationEntity(
            id = id,
            appName = appName,
            title = title,
            message = message,
            timestamp = timestamp,
            deviceId = deviceId,
            deviceName = deviceName,
            batchId = batchId,
            uniqueKey = uniqueKey
        )
    }
}
