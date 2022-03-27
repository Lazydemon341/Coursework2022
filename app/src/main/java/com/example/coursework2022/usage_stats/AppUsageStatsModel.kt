package com.example.coursework2022.usage_stats;

import android.app.usage.UsageStats
import android.graphics.drawable.Drawable

data class AppUsageStatsModel(
  val usageStats: UsageStats,
  val appLabel: String,
  val appIcon: Drawable?
)