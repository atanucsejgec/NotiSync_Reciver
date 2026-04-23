// ============================================================
// FILE: app/src/main/java/com/app/notisync_receiver/di/DatabaseModule.kt
// Purpose: Hilt module providing Room database and DAO
// ============================================================

package com.app.notisync_receiver.di

import android.content.Context
import androidx.room.Room
import com.app.notisync_receiver.data.local.ReceivedNotificationDao
import com.app.notisync_receiver.data.local.ReceiverDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideReceiverDatabase(
        @ApplicationContext context: Context
    ): ReceiverDatabase {
        return Room.databaseBuilder(
            context,
            ReceiverDatabase::class.java,
            "notisync_receiver_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideReceivedNotificationDao(
        database: ReceiverDatabase
    ): ReceivedNotificationDao {
        return database.receivedNotificationDao()
    }
}