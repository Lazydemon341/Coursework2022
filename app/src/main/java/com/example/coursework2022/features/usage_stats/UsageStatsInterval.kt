package com.example.coursework2022.features.usage_stats

import android.content.Context
import android.icu.util.Calendar
import androidx.annotation.StringRes
import com.example.coursework2022.R
import com.example.coursework2022.utils.getLastMidnightUTC
import com.example.coursework2022.utils.getNextMidnightUTC

enum class UsageStatsInterval(@StringRes val titleId: Int) {
  DAILY(R.string.interval_daily),
  WEEKLY(R.string.interval_weekly);

  fun getStartTime(): Long {
    return when (this) {
      DAILY -> getLastMidnightUTC()
      WEEKLY -> Calendar.getInstance().apply {
        timeInMillis = getLastMidnightUTC()
        add(Calendar.DAY_OF_YEAR, -7)
      }.timeInMillis
    }
  }

  fun getEndTime(): Long {
    return getNextMidnightUTC()
  }

  companion object {
    fun getValue(context: Context, title: String): UsageStatsInterval? {
      return values().firstOrNull { context.resources.getString(it.titleId) == title }
    }
  }
}