// ============================================================
// FILE: app/src/main/java/com/app/notisync_receiver/domain/model/SentenceBatch.kt
// Purpose: Represents a 2-hour batch of captured sentences
// ============================================================

package com.app.notisync_receiver.domain.model

data class SentenceBatch(
    val batchId: String,
    val userId: String,
    val deviceId: String,
    val deviceName: String,
    val batchTimestamp: Long,
    val sentences: List<CapturedSentence>
) {
    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromFirestore(map: Map<String, Any>): SentenceBatch {
            val sentencesList = map["sentences"] as? List<Map<String, Any>> ?: emptyList()
            val sentences = sentencesList.map { CapturedSentence.fromFirestore(it) }

            return SentenceBatch(
                batchId = map["batchId"] as? String ?: "",
                userId = map["userId"] as? String ?: "",
                deviceId = map["deviceId"] as? String ?: "",
                deviceName = map["deviceName"] as? String ?: "Unknown",
                batchTimestamp = (map["batchTimestamp"] as? Number)?.toLong() ?: 0L,
                sentences = sentences
            )
        }
    }
}
