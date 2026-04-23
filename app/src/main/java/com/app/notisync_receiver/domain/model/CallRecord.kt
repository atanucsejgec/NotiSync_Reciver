// ============================================================
// FILE: app/src/main/java/com/app/notisync_receiver/domain/model/CallRecord.kt
// Purpose: Represents a single call log entry from sender
// ============================================================

package com.app.notisync_receiver.domain.model

data class CallRecord(
    val id: String,
    val callerName: String?,
    val phoneNumber: String,
    val callType: String,
    val callDuration: Int,
    val timestamp: Long
) {
    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromFirestore(map: Map<String, Any?>): CallRecord {
            return CallRecord(
                id = map["id"] as? String ?: "",
                callerName = map["callerName"] as? String,
                phoneNumber = map["phoneNumber"] as? String ?: "",
                callType = map["callType"] as? String ?: "unknown",
                callDuration = (map["callDuration"] as? Number)?.toInt() ?: 0,
                timestamp = (map["timestamp"] as? Number)?.toLong() ?: 0L
            )
        }
    }
}
