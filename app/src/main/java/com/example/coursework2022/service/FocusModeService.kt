package com.example.coursework2022.service

import android.app.ActivityManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock
import android.widget.Toast
import com.example.coursework2022.App
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
      val pendingIntent = PendingIntent.getBroadcast(applicationContext, 0, receiverIntent, 0)
      manager[AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime] = pendingIntent

      killApp()
    } else {
      stopForeground(true)
      stopSelf()
    }
    return START_NOT_STICKY
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
      this.startActivity(startMain)
      (getSystemService(ACTIVITY_SERVICE) as ActivityManager).killBackgroundProcesses(packageName)
      Toast.makeText(applicationContext, R.string.app_not_allowed_msg, Toast.LENGTH_SHORT).show()
    }
  }

  companion object {
    //in seconds
    private const val UPDATA_INTERVAL = 0.1f
  }
}