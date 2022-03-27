package com.example.coursework2022.usage_stats

import android.app.usage.UsageStatsManager

enum class StatsUsageInterval(val mStringRepresentation: String, val mInterval: Int) {
  DAILY("Daily", UsageStatsManager.INTERVAL_DAILY),
  WEEKLY("Weekly", UsageStatsManager.INTERVAL_WEEKLY),
  MONTHLY("Monthly", UsageStatsManager.INTERVAL_MONTHLY);

  companion object {
    fun getValue(stringRepresentation: String): StatsUsageInterval? {
      for (statsUsageInterval in values()) {
        if (statsUsageInterval.mStringRepresentation == stringRepresentation) {
          return statsUsageInterval
        }
      }
      return null
    }
  }
}