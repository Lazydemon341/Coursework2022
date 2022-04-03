package com.example.coursework2022

import android.Manifest.permission
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.afollestad.materialdialogs.MaterialDialog
import com.example.coursework2022.features.about.AboutFragment
import com.example.coursework2022.features.schedules.SchedulesFragment
import com.example.coursework2022.features.usage_stats.UsageStatsProvider
import com.example.coursework2022.utils.AppInfosHolder
import com.example.coursework2022.utils.isPermissionGranted
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  @Inject
  lateinit var appInfosHolder: AppInfosHolder

  @Inject
  lateinit var usageStatsProvider: UsageStatsProvider

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    appInfosHolder.init()
    usageStatsProvider.init()
    setContentView(R.layout.activity_main)
    openViewPager()
    checkPermissions()
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.toolbar_menu, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.about -> {
        openAbout()
        return true
      }
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onSupportNavigateUp(): Boolean {
    onBackPressed()
    return true
  }

  fun openViewPager() {
    supportFragmentManager.commit {
      setCustomAnimations(R.animator.nav_default_enter_anim, R.animator.nav_default_exit_anim)
      replace<ViewPagerFragment>(R.id.container)
    }
  }

  fun openSchedules() {
    supportFragmentManager.commit {
      setCustomAnimations(R.animator.nav_default_enter_anim, R.animator.nav_default_exit_anim)
      replace<SchedulesFragment>(R.id.container)
      addToBackStack(null)
    }
  }

  fun openAbout() {
    supportFragmentManager.commit {
      setCustomAnimations(R.animator.nav_default_enter_anim, R.animator.nav_default_exit_anim)
      replace<AboutFragment>(R.id.container)
      addToBackStack(null)
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
        .message(text = "Please grant usage access permission")
        .positiveButton(text = "ok") {
          startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
        .show()
    }
  }
}