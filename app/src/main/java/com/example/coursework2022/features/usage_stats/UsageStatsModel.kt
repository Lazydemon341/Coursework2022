package com.example.coursework2022.features.usage_stats;

import android.graphics.drawable.Drawable

data class UsageStatsModel(
  val packageName: String,
  val usageTimeSeconds: Long = 0,
  val launchesCount: Int = 0,
  val lastTimeUsedMillis: Long? = null,
  val appLabel: String = "",
  val appIcon: Drawable? = null
)