// ============================================================
// FILE: app/src/main/java/com/app/notisync_receiver/viewmodel/CallLogViewModel.kt
// Purpose: Manages call log history for a specific device
// ============================================================

package com.app.notisync_receiver.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.notisync_receiver.data.repository.CallLogViewRepository
import com.app.notisync_receiver.domain.model.CallLogBatch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CallLogViewModel @Inject constructor(
    private val callLogRepository: CallLogViewRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val deviceId: String = savedStateHandle.get<String>("deviceId") ?: ""
    val deviceName: String = savedStateHandle.get<String>("deviceName") ?: "Unknown Device"

    private val _callLogBatches = MutableStateFlow<List<CallLogBatch>>(emptyList())
    val callLogBatches: StateFlow<List<CallLogBatch>> = _callLogBatches.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        observeCallLogs()
    }

    private fun observeCallLogs() {
        viewModelScope.launch {
            callLogRepository.observeCallLogs(deviceId).collect {
                _callLogBatches.value = it
                _isLoading.value = false
            }
        }
    }
}
