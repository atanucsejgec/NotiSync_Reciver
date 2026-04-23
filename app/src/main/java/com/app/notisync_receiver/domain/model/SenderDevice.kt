// ============================================================
// FILE: app/src/main/java/com/app/notisync_receiver/domain/model/SenderDevice.kt
// Purpose: Represents a sender device registered under the user
// ============================================================

package com.app.notisync_receiver.domain.model

data class SenderDevice(
    val deviceId: String,
    val deviceName: String,
    val lastSeen: Long,
    val isOnline: Boolean = false
) {
    companion object {
        private const val ONLINE_THRESHOLD_MS = 5 * 60 * 1000L

        fun fromFirestore(map: Map<String, Any>): SenderDevice {
            val deviceId = map["deviceId"] as? String ?: ""
            val deviceName = map["deviceName"] as? String ?: "Unknown Device"
            val lastSeen = (map["lastSeen"] as? Long) ?: 0L
            val isOnline = (System.currentTimeMillis() - lastSeen) < ONLINE_THRESHOLD_MS

            return SenderDevice(
                deviceId = deviceId,
                deviceName = deviceName,
                lastSeen = lastSeen,
                isOnline = isOnline
            )
        }
    }
}