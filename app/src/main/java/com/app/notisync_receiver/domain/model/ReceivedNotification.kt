// ============================================================
// FILE: app/src/main/java/com/app/notisync_receiver/domain/model/ReceivedNotification.kt
// Purpose: Represents a single notification received from a sender device
// ============================================================

package com.app.notisync_receiver.domain.model

data class ReceivedNotification(
    val id: String,
    val appName: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val deviceId: String,
    val deviceName: String,
    val batchId: String,
    val uniqueKey: String
) {
    companion object {
        fun fromFirestore(
            map: Map<String, Any>,
            deviceId: String,
            deviceName: String,
            batchId: String
        ): ReceivedNotification {
            val appName = map["appName"] as? String ?: ""
            val title = map["title"] as? String ?: ""
            val message = map["message"] as? String ?: ""
            val timestamp = (map["timestamp"] as? Long) ?: System.currentTimeMillis()
            val uniqueKey = map["uniqueKey"] as? String ?: "$appName|$title|$message|$timestamp"

            return ReceivedNotification(
                id = "${batchId}_${uniqueKey.hashCode()}",
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
}