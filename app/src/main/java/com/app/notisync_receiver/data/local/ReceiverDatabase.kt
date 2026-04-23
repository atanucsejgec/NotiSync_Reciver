// ============================================================
// FILE: app/src/main/java/com/app/notisync_receiver/data/local/ReceiverDatabase.kt
// Purpose: Room database definition for Receiver App
// ============================================================

package com.app.notisync_receiver.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ReceivedNotificationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ReceiverDatabase : RoomDatabase() {

    abstract fun receivedNotificationDao(): ReceivedNotificationDao
}