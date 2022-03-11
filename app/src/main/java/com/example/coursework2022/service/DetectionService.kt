package com.example.coursework2022.service

import android.accessibilityservice.AccessibilityService
import android.app.Service
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import com.example.coursework2022.R.string

class DetectionService : AccessibilityService() {

  override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
    return Service.START_STICKY
  }

  override fun onAccessibilityEvent(event: AccessibilityEvent) {
    if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
      foregroundPackageName = event.packageName.toString()
    }
  }

  override fun onInterrupt() {
  }

  companion object {
    var foregroundPackageName: String? = null
  }
}