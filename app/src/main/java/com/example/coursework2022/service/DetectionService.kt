package com.example.coursework2022.service

import android.accessibilityservice.AccessibilityService
import android.app.Service
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import com.example.coursework2022.PreferenceStorage
import com.example.coursework2022.R.string
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DetectionService : AccessibilityService() {

  @Inject
  lateinit var preferenceStorage: PreferenceStorage

  override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
    return Service.START_STICKY
  }

  override fun onAccessibilityEvent(event: AccessibilityEvent) {
    if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
      foregroundPackageName = event.packageName?.toString()
      print(preferenceStorage.isBlackListApp(packageName))
    }
  }

  override fun onInterrupt() {
  }

  companion object {
    var foregroundPackageName: String? = null
  }
}