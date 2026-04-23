// ============================================================
// FILE: app/src/main/java/com/app/notisync_receiver/viewmodel/KeyboardViewModel.kt
// Purpose: Manages keyboard capture history for a specific device
// ============================================================

package com.app.notisync_receiver.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.notisync_receiver.data.repository.KeyboardRepository
import com.app.notisync_receiver.domain.model.SentenceBatch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KeyboardViewModel @Inject constructor(
    private val keyboardRepository: KeyboardRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val deviceId: String = savedStateHandle.get<String>("deviceId") ?: ""
    val deviceName: String = savedStateHandle.get<String>("deviceName") ?: "Unknown Device"

    private val _sentenceBatches = MutableStateFlow<List<SentenceBatch>>(emptyList())
    val sentenceBatches: StateFlow<List<SentenceBatch>> = _sentenceBatches.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        observeSentences()
    }

    private fun observeSentences() {
        viewModelScope.launch {
            keyboardRepository.observeKeyboardSentences(deviceId).collect {
                _sentenceBatches.value = it
                _isLoading.value = false
            }
        }
    }
}
