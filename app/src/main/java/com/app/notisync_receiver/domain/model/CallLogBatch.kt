// ============================================================
// FILE: app/src/main/java/com/app/notisync_receiver/domain/model/CallLogBatch.kt
// Purpose: Represents a daily call log batch from sender
// ============================================================

package com.app.notisync_receiver.domain.model

data class CallLogBatch(
    val batchId: String,
    val userId: String,
    val deviceId: String,
    val deviceName: String,
    val batchDate: String,
    val batchTimestamp: Long,
    val calls: List<CallRecord>
) {
    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromFirestore(map: Map<String, Any>): CallLogBatch {
            val callsList = map["calls"] as? List<Map<String, Any?>> ?: emptyList()
            val calls = callsList.map { CallRecord.fromFirestore(it) }

            return CallLogBatch(
                batchId = map["batchId"] as? String ?: "",
                userId = map["userId"] as? String ?: "",
                deviceId = map["deviceId"] as? String ?: "",
                deviceName = map["deviceName"] as? String ?: "Unknown",
                batchDate = map["batchDate"] as? String ?: "",
                batchTimestamp = (map["batchTimestamp"] as? Number)?.toLong() ?: 0L,
                calls = calls
            )
        }
    }
}
