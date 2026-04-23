// ============================================================
// FILE: app/src/main/java/com/app/notisync_receiver/data/local/ReceivedNotificationDao.kt
// Purpose: DAO for received notification CRUD operations
// ============================================================

package com.app.notisync_receiver.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceivedNotificationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNotification(notification: ReceivedNotificationEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNotifications(notifications: List<ReceivedNotificationEntity>)

    @Query("SELECT * FROM received_notifications WHERE device_id = :deviceId ORDER BY timestamp DESC")
    fun observeNotificationsByDevice(deviceId: String): Flow<List<ReceivedNotificationEntity>>

    @Query("SELECT * FROM received_notifications ORDER BY timestamp DESC")
    fun observeAllNotifications(): Flow<List<ReceivedNotificationEntity>>

    @Query("SELECT * FROM received_notifications ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecentNotifications(limit: Int = 50): Flow<List<ReceivedNotificationEntity>>

    @Query("SELECT COUNT(*) FROM received_notifications WHERE is_read = 0")
    fun observeUnreadCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM received_notifications WHERE device_id = :deviceId AND is_read = 0")
    fun observeUnreadCountByDevice(deviceId: String): Flow<Int>

    @Query("SELECT EXISTS(SELECT 1 FROM received_notifications WHERE unique_key = :uniqueKey)")
    suspend fun exists(uniqueKey: String): Boolean

    @Query("UPDATE received_notifications SET is_read = 1 WHERE id = :notificationId")
    suspend fun markAsRead(notificationId: String)

    @Query("UPDATE received_notifications SET is_read = 1 WHERE device_id = :deviceId")
    suspend fun markAllAsReadByDevice(deviceId: String)

    @Query("UPDATE received_notifications SET is_read = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM received_notifications WHERE id = :notificationId")
    suspend fun deleteNotification(notificationId: String)

    @Query("DELETE FROM received_notifications WHERE device_id = :deviceId")
    suspend fun deleteNotificationsByDevice(deviceId: String)

    @Query("DELETE FROM received_notifications")
    suspend fun deleteAllNotifications()

    @Query("DELETE FROM received_notifications WHERE timestamp < :timestamp")
    suspend fun deleteOldNotifications(timestamp: Long)

    @Query("SELECT COUNT(*) FROM received_notifications")
    suspend fun getTotalCount(): Int

    @Query("SELECT DISTINCT device_id FROM received_notifications")
    suspend fun getDeviceIds(): List<String>
}