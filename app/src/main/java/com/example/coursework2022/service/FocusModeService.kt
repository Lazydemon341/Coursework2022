package com.example.coursework2022.service

import android.app.ActivityManager
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.coursework2022.App
import com.example.coursework2022.MainActivity
import com.example.coursework2022.PreferenceStorage
import com.example.coursework2022.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FocusModeService : Service() {

  @Inject
  lateinit var preferenceStorage: PreferenceStorage

  override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
    if (App.focusModeOn) {
      val manager = getSystemService(ALARM_SERVICE) as AlarmManager
      val updateTime = UPDATA_INTERVAL.toInt() * 1000
      val triggerAtTime = SystemClock.elapsedRealtime() + updateTime
      val receiverIntent = Intent(applicationContext, FocusModeReceiver::class.java)
      val broadcastIntent = PendingIntent.getBroadcast(applicationContext, 0, receiverIntent, FLAG_IMMUTABLE)
      manager[AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime] = broadcastIntent

      val pendingIntent: PendingIntent =
        Intent(this, MainActivity::class.java).let { notificationIntent ->
          PendingIntent.getActivity(this, 0, notificationIntent, FLAG_IMMUTABLE)
        }

      val notificationBuilder =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          Notification.Builder(this, createNotificationChannel("my_service", "My Background Service"))
        } else {
          // If earlier version channel ID is not used
          // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
          Notification.Builder(this)
        }

      val notification = notificationBuilder.apply {
        setContentTitle("my servce")
        setContentText("focus mode is on")
        setSmallIcon(R.drawable.ic_baseline_apps_24)
        setContentIntent(pendingIntent)
      }.build()
      startForeground(1, notification)

      killApp()
    } else {
      stopForeground(true)
      stopSelf()
    }
    return START_NOT_STICKY
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private fun createNotificationChannel(channelId: String, channelName: String): String {
    val chan = NotificationChannel(
      channelId,
      channelName, NotificationManager.IMPORTANCE_NONE
    )
    chan.lightColor = Color.BLUE
    chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
    val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    service.createNotificationChannel(chan)
    return channelId
  }

  override fun onDestroy() {
    super.onDestroy()
    stopForeground(true)
  }

  override fun onBind(p0: Intent?): IBinder? {
    return null
  }

  private fun killApp() {
    val packageName: String? = DetectionService.foregroundPackageName
    if (packageName != null && preferenceStorage.isBlackListApp(packageName)) {
      val startMain = Intent(Intent.ACTION_MAIN)
      startMain.addCategory(Intent.CATEGORY_HOME)
      startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
      startActivity(startMain)
      (getSystemService(ACTIVITY_SERVICE) as ActivityManager).killBackgroundProcesses(packageName)
      Toast.makeText(applicationContext, R.string.app_not_allowed_msg, Toast.LENGTH_SHORT).show()
    }
  }

  companion object {
    //in seconds
    private const val UPDATA_INTERVAL = 0.5f
  }
}