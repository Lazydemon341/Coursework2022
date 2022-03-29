package com.example.coursework2022.usage_stats;

import android.graphics.drawable.Drawable

data class AppUsageInfo(
  val packageName: String,
  val usageTimeSeconds: Int = 0,
  val launchesCount: Int = 0,
  val lastTimeUsedMillis: Long = 0,
  val appLabel: String = "",
  val appIcon: Drawable? = null
)