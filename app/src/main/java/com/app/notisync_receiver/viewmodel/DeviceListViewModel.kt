// ============================================================
// FILE: app/src/main/java/com/app/notisync_receiver/viewmodel/DeviceListViewModel.kt
// Purpose: Manages sender device list from Firestore
// ============================================================

package com.app.notisync_receiver.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.notisync_receiver.data.repository.AuthRepository
import com.app.notisync_receiver.data.repository.DeviceRepository
import com.app.notisync_receiver.data.repository.NotificationRepository
import com.app.notisync_receiver.domain.model.SenderDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceListViewModel @Inject constructor(
    private val deviceRepository: DeviceRepository,
    private val notificationRepository: NotificationRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _devices = MutableStateFlow<List<SenderDevice>>(emptyList())
    val devices: StateFlow<List<SenderDevice>> = _devices.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isDeleting = MutableStateFlow(false)
    val isDeleting: StateFlow<Boolean> = _isDeleting.asStateFlow()

    val unreadCount: StateFlow<Int> = notificationRepository
        .observeUnreadCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val userEmail: String?
        get() = authRepository.getCurrentUser()?.email

    init {
        observeDevices()
    }

    private fun observeDevices() {
        viewModelScope.launch {
            deviceRepository.observeDevices().collect { deviceList ->
                _devices.value = deviceList.sortedByDescending { it.lastSeen }
                _isLoading.value = false
            }
        }
    }

    fun refreshDevices() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = deviceRepository.getDevices()
            result.onSuccess { deviceList ->
                _devices.value = deviceList.sortedByDescending { it.lastSeen }
            }.onFailure { error ->
                _errorMessage.value = error.message
            }

            _isLoading.value = false
        }
    }

    fun deleteDevice(deviceId: String) {
        viewModelScope.launch {
            _isDeleting.value = true
            val result = deviceRepository.deleteDevice(deviceId)
            result.onSuccess {
                // Also clear local notifications for this device
                notificationRepository.deleteNotificationsByDevice(deviceId)
            }.onFailure { error ->
                _errorMessage.value = "Failed to delete device: ${error.message}"
            }
            _isDeleting.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            notificationRepository.clearAllNotifications()
        }
    }

    fun logout() {
        viewModelScope.launch {
            notificationRepository.clearAllNotifications()
        }
        authRepository.logout()
    }
}
