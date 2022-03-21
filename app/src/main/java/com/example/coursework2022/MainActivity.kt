package com.example.coursework2022

import android.Manifest.permission
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
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
        when (item.itemId) {
          R.id.usage_stats -> {
            supportFragmentManager.commit {
              replace(R.id.container, UsageStatsFragment.newInstance())
            }
            true
          }
          R.id.focus_mode -> {
            supportFragmentManager.commit {
              replace(R.id.container, FocusModeFragment.newInstance())
            }
            true
          }
          R.id.black_list -> {
            supportFragmentManager.commit {
              replace(R.id.container, BlackListFragment.newInstance())
            }
            true
          }
          else -> false
        }
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