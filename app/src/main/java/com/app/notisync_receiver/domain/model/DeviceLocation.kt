// ============================================================
// FILE: app/src/main/java/com/app/notisync_receiver/domain/model/DeviceLocation.kt
// Purpose: Represents a location received from a sender device
// ============================================================

package com.app.notisync_receiver.domain.model

data class DeviceLocation(
    val locationId: String,
    val userId: String,
    val deviceId: String,
    val deviceName: String,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val timestamp: Long,
    val provider: LocationProvider,
    val requestId: String? = null
) {
    enum class LocationProvider {
        GPS, NETWORK, FUSED, CELL_ID, IP, UNKNOWN;

        companion object {
            fun fromString(value: String?): LocationProvider {
                return when (value?.uppercase()) {
                    "GPS" -> GPS
                    "NETWORK" -> NETWORK
                    "FUSED" -> FUSED
                    "CELL_ID" -> CELL_ID
                    "IP" -> IP
                    else -> UNKNOWN
                }
            }
        }
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromFirestore(map: Map<String, Any>): DeviceLocation {
            return DeviceLocation(
                locationId = map["locationId"] as? String ?: "",
                userId = map["userId"] as? String ?: "",
                deviceId = map["deviceId"] as? String ?: "",
                deviceName = map["deviceName"] as? String ?: "Unknown",
                latitude = (map["latitude"] as? Number)?.toDouble() ?: 0.0,
                longitude = (map["longitude"] as? Number)?.toDouble() ?: 0.0,
                accuracy = (map["accuracy"] as? Number)?.toFloat() ?: 0f,
                timestamp = (map["timestamp"] as? Number)?.toLong() ?: 0L,
                provider = LocationProvider.fromString(map["provider"] as? String),
                requestId = map["requestId"] as? String
            )
        }
    }
}
