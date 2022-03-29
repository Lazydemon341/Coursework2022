package com.example.coursework2022.usage_stats

import android.content.Context
import android.icu.util.Calendar
import androidx.annotation.StringRes
import com.example.coursework2022.R
import com.example.coursework2022.utils.getLastMidnight
import com.example.coursework2022.utils.getNextMidnightMillis

enum class StatsUsageInterval(@StringRes val titleId: Int) {
  DAILY(R.string.interval_daily),
  WEEKLY(R.string.interval_weekly);

  fun getStartTime(): Long {
    val date = when (this) {
      DAILY -> getLastMidnight()
      WEEKLY -> getLastMidnight().apply { add(Calendar.DAY_OF_MONTH, -7) }
    }
    return date.timeInMillis
  }

  fun getEndTime(): Long {
    return getNextMidnightMillis()
  }

  companion object {
    fun getValue(context: Context, title: String): StatsUsageInterval? {
      return values().firstOrNull { context.resources.getString(it.titleId) == title }
    }
  }
}