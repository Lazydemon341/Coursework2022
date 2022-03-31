package com.example.coursework2022.usage_stats;

import android.graphics.drawable.Drawable

data class AppUsageInfo(
  val packageName: String,
  val usageTimeSeconds: Long = 0,
  val launchesCount: Int = 0,
  val lastTimeUsedMillis: Long? = null,
  val appLabel: String = "",
  val appIcon: Drawable? = null
)