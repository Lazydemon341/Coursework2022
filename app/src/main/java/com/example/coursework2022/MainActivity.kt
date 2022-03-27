package com.example.coursework2022

import android.Manifest.permission
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import com.example.coursework2022.usage_stats.AppUsageStatisticsFragment
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
          R.id.usage_stats -> AppUsageStatisticsFragment.newInstance()
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
    if (ContextCompat.checkSelfPermission(this, permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this, arrayOf(permission.READ_PHONE_STATE), 100)
    }
  }
}