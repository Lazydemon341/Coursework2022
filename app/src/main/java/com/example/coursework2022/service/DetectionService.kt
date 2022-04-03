package com.example.coursework2022.service

import android.accessibilityservice.AccessibilityService
import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.coursework2022.MainActivity
import com.example.coursework2022.PreferenceStorage
import com.example.coursework2022.R
import com.example.coursework2022.features.schedules.ScheduleModelsHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val NOTIFICATION_ID = 1

@AndroidEntryPoint
class DetectionService : AccessibilityService() {

  @Inject
  lateinit var preferenceStorage: PreferenceStorage

  @Inject
  lateinit var scheduleModelsHolder: ScheduleModelsHolder

  private var isForeground = false

  override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
    return Service.START_STICKY
  }

  override fun onServiceConnected() {
    super.onServiceConnected()
    CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
      preferenceStorage.focusModeStatusFlow.collect {
        updateForeground(it)
      }
    }
  }

  override fun onAccessibilityEvent(event: AccessibilityEvent) {
//    if (scheduleModelsHolder.getSchedules().any { it.isGoingNow() }) {
//      preferenceStorage.setFocusModeStatus(true)
//    }
    val focusModeOn = preferenceStorage.getFocusModeStatus()
    if (focusModeOn && event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
      event.packageName?.toString()?.let { killAppIfInBlacklist(it) }
    }
  }

  override fun onInterrupt() {
    // no-op
  }

  private fun updateForeground(focusModeOn: Boolean) {
    if (focusModeOn && !isForeground) {
      val pendingIntent: PendingIntent =
        Intent(this, MainActivity::class.java).let { notificationIntent ->
          PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        }
      val notification = Notification.Builder(this, createNotificationChannel())
        .apply {
          setContentTitle("UsageManager")
          setContentText("FocusMode is active")
          setSmallIcon(R.drawable.ic_launcher)
          setContentIntent(pendingIntent)
        }.build()
      startForeground(NOTIFICATION_ID, notification)
      isForeground = true
    } else if (!focusModeOn && isForeground) {
      stopForeground(true)
      isForeground = false
    }
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private fun createNotificationChannel(): String {
    val channel = NotificationChannel(
      "my_service",
      "My Background Service",
      NotificationManager.IMPORTANCE_NONE
    )
    channel.lightColor = Color.BLUE
    channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
    val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    service.createNotificationChannel(channel)
    return "my_service"
  }

  private fun killAppIfInBlacklist(packageName: String) {
    if (preferenceStorage.isBlackListApp(packageName)) {
      val launcherIntent = Intent(Intent.ACTION_MAIN)
      launcherIntent.addCategory(Intent.CATEGORY_HOME)
      launcherIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
      startActivity(launcherIntent)
      (getSystemService(ACTIVITY_SERVICE) as ActivityManager).killBackgroundProcesses(packageName)
      Toast.makeText(applicationContext, R.string.app_not_allowed_msg, Toast.LENGTH_SHORT).show()
    }
  }
}