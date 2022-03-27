package com.example.coursework2022

import android.Manifest.permission
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.commit
import com.afollestad.materialdialogs.MaterialDialog
import com.example.coursework2022.usage_stats.UsageStatsFragment
import com.example.coursework2022.utils.isPermissionGranted
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setupBottomNav()
    checkPermissions()
  }

  private fun setupBottomNav() {
    findViewById<BottomNavigationView>(R.id.bottom_navigation).apply {
      setOnItemSelectedListener { item ->
        val fragment = when (item.itemId) {
          R.id.usage_stats -> UsageStatsFragment.newInstance()
          R.id.focus_mode -> FocusModeFragment.newInstance()
          R.id.black_list -> BlackListFragment.newInstance()
          else -> return@setOnItemSelectedListener false
        }
        supportFragmentManager.commit { replace(R.id.container, fragment) }
        return@setOnItemSelectedListener true
      }
      selectedItemId = R.id.focus_mode
    }
  }

  private fun checkPermissions() {
    if (!isPermissionGranted(permission.READ_PHONE_STATE)) {
      ActivityCompat.requestPermissions(this, arrayOf(permission.READ_PHONE_STATE), 100)
    }
    val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOpsManager.checkOpNoThrow("android:get_usage_stats", android.os.Process.myUid(), packageName);
    if (mode != AppOpsManager.MODE_ALLOWED) {
      MaterialDialog(this)
        .title(text = "Permission required")
        .message(text = "Please in the settigns")
        .positiveButton(text = "ok") {
          startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
        .negativeButton(text = "cancel")
        .show()
    }
  }
}