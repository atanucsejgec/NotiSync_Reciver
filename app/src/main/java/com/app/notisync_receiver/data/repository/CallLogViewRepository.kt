// ============================================================
// FILE: app/src/main/java/com/app/notisync_receiver/data/repository/CallLogViewRepository.kt
// Purpose: Reads call log data from Firestore
// ============================================================

package com.app.notisync_receiver.data.repository

import com.app.notisync_receiver.data.remote.FirestoreDataSource
import com.app.notisync_receiver.domain.model.CallLogBatch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallLogViewRepository @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource,
    private val authRepository: AuthRepository
) {

    fun observeCallLogs(deviceId: String): Flow<List<CallLogBatch>> {
        val userId = authRepository.getCurrentUserId() ?: return emptyFlow()
        return firestoreDataSource.observeCallLogBatches(userId, deviceId)
    }
}
