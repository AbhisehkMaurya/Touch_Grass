package com.tracking.touchgrass.android.viewmodel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TouchGrassViewModel : androidx.lifecycle.ViewModel() {
    private val _isCameraPermissionGranted = MutableStateFlow(false)
    val isCameraPermissionGranted: StateFlow<Boolean> = _isCameraPermissionGranted

    private val _touchVerified = MutableStateFlow(false)
    val touchVerified: StateFlow<Boolean> = _touchVerified

    private val _appsBlocked = MutableStateFlow(true)
    val appsBlocked: StateFlow<Boolean> = _appsBlocked

    private val _message = MutableStateFlow("Camera permission is required to use this feature.")
    val message: String
        get() = _message.value

    fun setCameraPermissionGranted(granted: Boolean) {
        _isCameraPermissionGranted.value = granted
    }

    fun updateMessage(newMessage: String) {
        _message.value = newMessage
    }

    fun app(unblock: Boolean) {
        _appsBlocked.value = !unblock
    }

    fun setAppsBlocked(blocked: Boolean) {
        _appsBlocked.value = blocked
    }

    fun onVerifyTouch() {
        viewModelScope.launch {
            // Simulate verification logic (replace with actual verification logic)
            _touchVerified.value = true
        }
    }
}