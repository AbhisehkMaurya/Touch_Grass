package com.tracking.touchgrass.android

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent

class AppLockService : AccessibilityService() {
    private val lockedApps = listOf("com.facebook.katana", "com.whatsapp") // Add locked app package names

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString()
            if (lockedApps.contains(packageName)) {
                showLockScreen()
            }
        }
    }

    private fun showLockScreen() {
        val intent = LockScreenActivity.newIntent(this)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onInterrupt() {
        // Handle service interruptions
    }
}
