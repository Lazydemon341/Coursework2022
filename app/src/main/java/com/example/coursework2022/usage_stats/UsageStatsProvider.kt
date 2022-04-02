package com.example.coursework2022.usage_stats

import android.app.usage.UsageEvents.Event
import android.app.usage.UsageStatsManager
import android.content.Context
import com.example.coursework2022.usage_stats.StatsUsageInterval.DAILY
import com.example.coursework2022.utils.AppInfosHolder
import com.example.coursework2022.utils.getAppsList
import com.example.coursework2022.utils.getMidnightUtcMinusDays
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UsageStatsProvider @Inject constructor(
  @ApplicationContext
  private val context: Context,
  private val appInfosHolder: AppInfosHolder
) {

  private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
  private val mainPackageNames = context.getAppsList().map { it.activityInfo.packageName }
  private val usageStatsByInterval: MutableMap<StatsUsageInterval, List<AppUsageInfo>> = mutableMapOf()
  private val weekUsageTime: ArrayList<Long> = arrayListOf()
  private val usageStatsWeek: ArrayList<List<AppUsageInfo>> = arrayListOf()

  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

  fun init() {
    scope.launch {
      StatsUsageInterval.values().forEach {
        getAndSaveUsageStats(it)
      }
    }
    scope.launch {
      getWeekUsageStats()
    }
  }

  fun getWeekUsageTime(): ArrayList<Long> {
    if (weekUsageTime.isEmpty()) {
      getWeekUsageStats()
    }
    return weekUsageTime
  }

  suspend fun getUsageStats(interval: StatsUsageInterval): List<AppUsageInfo> {
    val savedUsageStats = usageStatsByInterval[interval]
    return if (savedUsageStats.isNullOrEmpty()) {
      getAndSaveUsageStats(interval)
    } else {
      savedUsageStats
    }
  }

  private suspend fun getAndSaveUsageStats(interval: StatsUsageInterval): List<AppUsageInfo> {
    return withContext(Dispatchers.Default) {
      getUsageStatsInternal(interval.getStartTime(), interval.getEndTime())
        .also {
          usageStatsByInterval[interval] = it
        }
    }
  }

  private fun getUsageStatsInternal(startTime: Long, endTime: Long): List<AppUsageInfo> {
    var currentEvent: Event
    val usageEvents: MutableList<Event> = ArrayList()
    val appUsageMap: MutableMap<String, UsageTimeAndLaunches> = mutableMapOf()
    val query = usageStatsManager.queryEvents(startTime, endTime) ?: return emptyList()

    while (query.hasNextEvent()) {
      currentEvent = Event()
      query.getNextEvent(currentEvent)
      if (mainPackageNames.contains(currentEvent.packageName)
        && (currentEvent.eventType == Event.ACTIVITY_RESUMED
            || currentEvent.eventType == Event.ACTIVITY_PAUSED)
      ) {
        usageEvents.add(currentEvent)
        val key = currentEvent.packageName
        if (appUsageMap[key] == null) appUsageMap[key] = UsageTimeAndLaunches()
      }
    }

    for (i in 0 until usageEvents.size - 1) {
      currentEvent = usageEvents[i]
      val nextEvent = usageEvents[i + 1]
      var prevValue = appUsageMap[currentEvent.packageName] ?: UsageTimeAndLaunches()

      if (currentEvent.eventType == Event.ACTIVITY_RESUMED) {
        // increment launches
        prevValue = UsageTimeAndLaunches(
          launchesCount = prevValue.launchesCount + 1,
          usageTimeSeconds = prevValue.usageTimeSeconds,
          lastTimeUsed = currentEvent.timeStamp
        )
      }
      if (currentEvent.className == nextEvent.className
        && nextEvent.eventType == Event.ACTIVITY_PAUSED
      ) {
        // add usage time
        val diffSeconds = (nextEvent.timeStamp - currentEvent.timeStamp) / 1000
        appUsageMap[currentEvent.packageName] = UsageTimeAndLaunches(
          launchesCount = prevValue.launchesCount,
          usageTimeSeconds = prevValue.usageTimeSeconds + diffSeconds,
          lastTimeUsed = prevValue.lastTimeUsed
        )
      }
    }

    usageEvents.lastOrNull()?.let { lastEvent ->
      if (lastEvent.eventType == Event.ACTIVITY_RESUMED) {
        val endOfSession = if (System.currentTimeMillis() > endTime) endTime else System.currentTimeMillis()
        val diffSeconds = (endOfSession - lastEvent.timeStamp) / 1000
        val prevValue = appUsageMap[lastEvent.packageName] ?: UsageTimeAndLaunches()
        appUsageMap[lastEvent.packageName] = UsageTimeAndLaunches(
          launchesCount = prevValue.launchesCount + 1,
          usageTimeSeconds = prevValue.usageTimeSeconds + diffSeconds,
          lastTimeUsed = lastEvent.timeStamp
        )
      }
    }

    return appUsageMap
      .map {
        val value = it.value
        AppUsageInfo(
          packageName = it.key,
          usageTimeSeconds = value.usageTimeSeconds,
          lastTimeUsedMillis = value.lastTimeUsed,
          launchesCount = value.launchesCount,
          appLabel = appInfosHolder.getAppLabel(it.key),
          appIcon = appInfosHolder.getAppIcon(it.key)
        )
      }
      .filter {
        it.usageTimeSeconds > 0
      }
  }

  private fun getWeekUsageStats() {
    weekUsageTime.clear()
    usageStatsWeek.clear()
    for (i in 7 downTo 1) {
      val tmp = if (i == 1)
        usageStatsByInterval[DAILY] ?: getUsageStatsInternal(getMidnightUtcMinusDays(i), getMidnightUtcMinusDays(i - 1))
      else
        getUsageStatsInternal(getMidnightUtcMinusDays(i), getMidnightUtcMinusDays(i - 1))
      usageStatsWeek.add(tmp)
      val sum = tmp.sumOf { it.usageTimeSeconds }
      weekUsageTime.add(sum)
    }
  }
}

private class UsageTimeAndLaunches(
  var launchesCount: Int = 0,
  var usageTimeSeconds: Long = 0,
  var lastTimeUsed: Long? = null
)