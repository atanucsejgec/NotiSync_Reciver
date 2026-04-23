// ============================================================
// FILE: app/src/main/java/com/app/notisync_receiver/viewmodel/NotificationListViewModel.kt
// Purpose: Manages notifications for a specific sender device
// ============================================================

package com.app.notisync_receiver.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.notisync_receiver.data.repository.NotificationRepository
import com.app.notisync_receiver.domain.model.ReceivedNotification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationListViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val deviceId: String = savedStateHandle.get<String>("deviceId") ?: ""
    val deviceName: String = savedStateHandle.get<String>("deviceName") ?: "Unknown Device"

    private val _notifications = MutableStateFlow<List<ReceivedNotification>>(emptyList())
    val notifications: StateFlow<List<ReceivedNotification>> = _notifications.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode: StateFlow<Boolean> = _isSelectionMode.asStateFlow()

    private val _selectedIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedIds: StateFlow<Set<String>> = _selectedIds.asStateFlow()

    private val _isDeleting = MutableStateFlow(false)
    val isDeleting: StateFlow<Boolean> = _isDeleting.asStateFlow()

    val unreadCount: StateFlow<Int> = notificationRepository
        .observeUnreadCountByDevice(deviceId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        observeNotifications()
        syncNotifications()
    }

    private fun observeNotifications() {
        viewModelScope.launch {
            notificationRepository.observeNotificationsFromFirestore(deviceId)
                .collect { batches ->
                    notificationRepository.saveNotificationsFromBatches(batches)
                }
        }

        viewModelScope.launch {
            notificationRepository.observeLocalNotificationsByDevice(deviceId)
                .collect { notificationList ->
                    _notifications.value = notificationList
                    _isLoading.value = false
                }
        }
    }

    private fun syncNotifications() {
        notificationRepository.syncNotifications(deviceId)
    }

    fun refresh() {
        _isLoading.value = true
        syncNotifications()
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            notificationRepository.markAsRead(notificationId)
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            notificationRepository.markAllAsReadByDevice(deviceId)
        }
    }

    fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            notificationRepository.deleteNotification(notificationId)
        }
    }

    fun deleteNotificationFromCloud(batchId: String) {
        viewModelScope.launch {
            notificationRepository.deleteNotificationFromFirestore(deviceId, batchId)
        }
    }

    fun deleteAllNotifications() {
        viewModelScope.launch {
            _isDeleting.value = true
            notificationRepository.deleteAllNotificationsFromFirestore(deviceId)
            _isDeleting.value = false
        }
    }

    fun toggleSelectionMode() {
        _isSelectionMode.value = !_isSelectionMode.value
        if (!_isSelectionMode.value) {
            _selectedIds.value = emptySet()
        }
    }

    fun toggleSelection(notificationId: String) {
        val current = _selectedIds.value.toMutableSet()
        if (current.contains(notificationId)) {
            current.remove(notificationId)
        } else {
            current.add(notificationId)
        }
        _selectedIds.value = current
    }

    fun selectAll() {
        _selectedIds.value = _notifications.value.map { it.id }.toSet()
    }

    fun deselectAll() {
        _selectedIds.value = emptySet()
    }

    fun deleteSelected() {
        viewModelScope.launch {
            _isDeleting.value = true
            val selected = _selectedIds.value.toList()
            for (id in selected) {
                notificationRepository.deleteNotification(id)
            }
            _selectedIds.value = emptySet()
            _isSelectionMode.value = false
            _isDeleting.value = false
        }
    }

    val selectedCount: Int
        get() = _selectedIds.value.size
}
