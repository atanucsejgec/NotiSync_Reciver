// ============================================================
// FILE: app/src/main/java/com/app/notisync_receiver/domain/model/NotificationBatch.kt
// Purpose: Represents a batch of notifications from Firestore
// ============================================================

package com.app.notisync_receiver.domain.model

data class NotificationBatch(
    val batchId: String,
    val userId: String,
    val deviceId: String,
    val deviceName: String,
    val batchTimestamp: Long,
    val notifications: List<ReceivedNotification>
) {
    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromFirestore(map: Map<String, Any>): NotificationBatch {
            val batchId = map["batchId"] as? String ?: ""
            val userId = map["userId"] as? String ?: ""
            val deviceId = map["deviceId"] as? String ?: ""
            val deviceName = map["deviceName"] as? String ?: "Unknown"
            val batchTimestamp = (map["batchTimestamp"] as? Long) ?: 0L

            val notificationsList = map["notifications"] as? List<Map<String, Any>> ?: emptyList()
            val notifications = notificationsList.map { notifMap ->
                ReceivedNotification.fromFirestore(notifMap, deviceId, deviceName, batchId)
            }

            return NotificationBatch(
                batchId = batchId,
                userId = userId,
                deviceId = deviceId,
                deviceName = deviceName,
                batchTimestamp = batchTimestamp,
                notifications = notifications
            )
        }
    }
}