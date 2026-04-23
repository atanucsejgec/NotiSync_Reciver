// ============================================================
// FILE: app/src/main/java/com/app/notisync_receiver/viewmodel/LocationViewModel.kt
// Purpose: Manages real-time location tracking for a specific device
// ============================================================

package com.app.notisync_receiver.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.notisync_receiver.data.repository.LocationRequestRepository
import com.app.notisync_receiver.domain.model.DeviceLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationRepository: LocationRequestRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val deviceId: String = savedStateHandle.get<String>("deviceId") ?: ""
    val deviceName: String = savedStateHandle.get<String>("deviceName") ?: "Unknown Device"

    private val _locations = MutableStateFlow<List<DeviceLocation>>(emptyList())
    val locations: StateFlow<List<DeviceLocation>> = _locations.asStateFlow()

    private val _isRequesting = MutableStateFlow(false)
    val isRequesting: StateFlow<Boolean> = _isRequesting.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        observeLocations()
    }

    private fun observeLocations() {
        viewModelScope.launch {
            locationRepository.observeDeviceLocations(deviceId).collect {
                _locations.value = it
            }
        }
    }

    fun requestLocationUpdate() {
        viewModelScope.launch {
            _isRequesting.value = true
            val result = locationRepository.requestLocation(deviceId)
            result.onFailure {
                _errorMessage.value = "Failed to request location: ${it.message}"
            }
            _isRequesting.value = false
        }
    }

    fun deleteLocation(locationId: String) {
        viewModelScope.launch {
            locationRepository.deleteLocation(deviceId, locationId)
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
